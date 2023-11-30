package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.*;
import dev.banji.LibReserve.model.LibraryOccupancyQueue;
import dev.banji.LibReserve.model.Reservation;
import dev.banji.LibReserve.model.Student;
import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import dev.banji.LibReserve.model.dtos.StudentReservationDto;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import dev.banji.LibReserve.repository.StudentRepository;
import dev.banji.LibReserve.repository.StudentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.LongStream;

import static dev.banji.LibReserve.model.enums.ReservationStatus.*;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final LibraryConfigurationProperties libraryConfigurationProperties;
    private final StudentRepository studentRepository;
    private final StudentReservationRepository studentReservationRepository;
    private final ReservationCodeService reservationCodeService;
    private final LibraryOccupancyQueue libraryOccupancyQueue;
    private final JwtTokenService jwtTokenService;

    private StudentReservation reservationHandler(String matricNumber, Boolean isWalkInAccess, LocalDateTime proposedDateAndTime, Duration duration, Boolean isTodayBooking) {

        //check if there's the library is operational...
        if (!libraryConfigurationProperties.getAcceptingBookings())
            throw LibraryClosedException.LibraryNotOperationalException();

        //check if library has an available seat for the proposed time...
        Long availableSeat = internalReservationResolver(matricNumber, duration, isWalkInAccess, proposedDateAndTime, isTodayBooking);

        //check if the time is higher than permitted
        if (duration.toMinutes() > libraryConfigurationProperties.getBookingTimeAllowedInMinutes())
            throw new BookingTimeExceedsLimitException(libraryConfigurationProperties.getBookingTimeAllowedInMinutes());

        //generate reservationCode
        String generatedReservationCode = reservationCodeService.generateNewReservationCode();

        //the call to the student repository is to simply have access to the student object...
        Student student = studentRepository.findByMatricNumber(matricNumber).orElseThrow(() -> {
            throw UserNotFoundException.StudentNotFoundException();
        });

        //create reservation...
        StudentReservation studentReservation = StudentReservation.builder().checkInTime(proposedDateAndTime.toLocalTime()).seatNumber(availableSeat).intendedStay(duration).reservationCreationDate(LocalDate.now()).reservationCreationTime(LocalTime.now()).dateReservationWasMadeFor(proposedDateAndTime.toLocalDate()).timeReservationWasMadeFor(proposedDateAndTime.toLocalTime()).reservationStatus(BOOKED).student(student).reservationCode(generatedReservationCode).build();

        //persist to db...
        student.getStudentReservationList().add(studentReservation);
        studentRepository.save(student);
        // return the studentReservation
        return studentReservation;
    }

    public String reserveForTodayRequest(String matricNumber, LocalDateTime proposedDateAndTime, Duration duration) {
        return reservationHandler(matricNumber, false, proposedDateAndTime, duration, true).getReservationCode();
    }

    public String handleWalkInRequest(String matricNumber, Duration duration) {
        StudentReservation studentReservation = reservationHandler(matricNumber, true, LocalDateTime.now(), duration, true);
        boolean signedIn = libraryOccupancyQueue.signInStudent(new CurrentStudentDetailDto(matricNumber, studentReservation));
        if (!signedIn) throw new LibraryRuntimeException();
        return studentReservation.getReservationCode();
    }

    public String handleAdvancedRequest(String matricNumber, LocalDateTime proposedDateAndTime, Duration duration) {
        return reservationHandler(matricNumber, false, proposedDateAndTime, duration, false).getReservationCode();
    }

    public Boolean requestForExtension(String matricNumber, Duration extensionDuration) {
        //fetch reservation...
        StudentReservation reservation = (StudentReservation) libraryOccupancyQueue.isUserPresentInLibrary(matricNumber)
                .orElseThrow(() -> {
                    throw new StudentNotInLibraryException();
                });

        if (!libraryConfigurationProperties.getAllowTimeExtension()) // if time extensionDuration is not allowed
            throw new TimeExtensionNotPermittedException();
        if (!libraryConfigurationProperties.getAllowMultipleTimeExtension()) //if time multiple time extension is not allowed
            throw new MultipleTimeExtensionException();
        if (extensionDuration.toMinutes() > libraryConfigurationProperties.getMaximumTimeExtensionAllowedInMinutes()) //check the time duration
            throw new DurationExceedsLimitException();

        //update reservation...
        reservation.setStayExtended(true);
        Duration currentExtensionDuration = reservation.getTotalExtensionDuration();
        Duration totalDuration = currentExtensionDuration.plus(extensionDuration);

        reservation.setReservationStatus(TIME_EXTENDED);
        reservation.setTotalExtensionDuration(totalDuration);
        studentReservationRepository.save(reservation);
        // TODO send notification...
        return true;
    }

    public boolean studentLogout(Jwt jwt, String matricNumber) { //this means that the user should always be prompted before he/she is logged out.because logging out will invalidate his/her jwt token...
        //add JWT to blacklist
        boolean blackListed = jwtTokenService.blacklistAccessToken(jwt);

        if (libraryOccupancyQueue.isUserPresentInLibrary(matricNumber).isPresent())
            return checkoutStudent(matricNumber) && blackListed;
        return blackListed;
    }

    private boolean checkoutStudent(String matricNumber) { //meant to be used by the student...
        StudentReservation reservation = (StudentReservation) libraryOccupancyQueue.isUserPresentInLibrary(matricNumber).orElseThrow(() -> {
            throw new StudentNotInLibraryException();
        });
        reservation.setReservationStatus(CHECKED_OUT);
        reservation.setCheckOutDateAndTime(LocalDateTime.now());
        studentReservationRepository.save(reservation);
        return libraryOccupancyQueue.signOutStudent(new CurrentStudentDetailDto(matricNumber, reservation));
    }

    public StudentReservationDto retrieveLastReservation(String matricNumber) {
        Optional<StudentReservation> optionalStudentReservation = studentReservationRepository.findFirstByStudentMatricNumber(matricNumber);

        StudentReservation studentReservation = optionalStudentReservation.orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });

        return StudentReservationDto.builder().seatNumber(studentReservation.getSeatNumber()).reservationCreationDateTime(LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime())).intendedUsageDateTime(LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor())).intendedStay(studentReservation.getIntendedStay()).checkOutDateAndTime(studentReservation.getCheckOutDateAndTime()).reservationStatus(studentReservation.getReservationStatus()).matricNumber(matricNumber).stayExtended(studentReservation.isStayExtended()).totalExtensionDuration(studentReservation.getTotalExtensionDuration()).build();

    }

    public List<StudentReservationDto> fetchAllReservations(String matricNumber) {
        List<StudentReservation> reservationList = studentReservationRepository.findByStudentMatricNumber(matricNumber);
        if (reservationList.isEmpty()) throw new ReservationDoesNotExistException();
        return reservationList.stream().map(studentReservation -> StudentReservationDto.builder().seatNumber(studentReservation.getSeatNumber()).reservationCreationDateTime(LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime())).intendedUsageDateTime(LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor())).intendedStay(studentReservation.getIntendedStay()).checkOutDateAndTime(studentReservation.getCheckOutDateAndTime()).reservationStatus(studentReservation.getReservationStatus()).matricNumber(studentReservation.getStudent().getMatricNumber()).stayExtended(studentReservation.isStayExtended()).totalExtensionDuration(studentReservation.getTotalExtensionDuration()).build()).toList();
    }

    public List<StudentReservationDto> fetchReservationsByStatus(String matricNumber, ReservationStatus reservationStatus) {
        var reservationList = studentReservationRepository.findByReservationStatusAndStudentMatricNumber(reservationStatus, matricNumber);
        if (reservationList.isEmpty()) throw new ReservationDoesNotExistException();
        return reservationList.stream().map(studentReservation -> StudentReservationDto.builder().seatNumber(studentReservation.getSeatNumber()).reservationCreationDateTime(LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime())).intendedUsageDateTime(LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor())).intendedStay(studentReservation.getIntendedStay()).checkOutDateAndTime(studentReservation.getCheckOutDateAndTime()).reservationStatus(studentReservation.getReservationStatus()).matricNumber(matricNumber).stayExtended(studentReservation.isStayExtended()).totalExtensionDuration(studentReservation.getTotalExtensionDuration()).build()).toList();
    }

    public boolean cancelAllReservations(String matricNumber) {
        List<StudentReservation> reservationList = studentReservationRepository.findByStudentMatricNumberAndReservationStatus(matricNumber, BOOKED);
        if (reservationList.isEmpty()) return false;
        reservationList.forEach(reservation -> {
            reservation.setReservationStatus(CANCELLED);
            studentReservationRepository.save(reservation);
        });
        studentReservationRepository.saveAll(reservationList);
        return true;
    }

    public boolean cancelReservationsByCode(String matricNumber, List<String> reservationCodesList) {
        reservationCodesList.forEach(reservationCode -> cancelReservationByCode(reservationCode, matricNumber));
        return true;
    }

    public boolean cancelLastReservation(String matricNumber) {
        StudentReservation reservation = studentReservationRepository.findByReservationStatusAndStudentMatricNumber(BOOKED, matricNumber).get(0);
        reservation.setReservationStatus(CANCELLED);
        studentReservationRepository.save(reservation);
        return true;
    }

    public boolean cancelReservationByCode(String reservationCode, String matricNumber) {
        StudentReservation reservation = studentReservationRepository.findByReservationCodeAndStudentMatricNumber(reservationCode, matricNumber).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });
        reservation.setReservationStatus(CANCELLED);
        studentReservationRepository.save(reservation);
        return true;
    }

    private Long internalReservationResolver(String matricNumber, Duration duration, Boolean walkInAccess, LocalDateTime proposedDateAndTime, Boolean todayBooking) {

        if (walkInAccess) { //TODO maybe implement an internal Reservation Resolver...
            //If student want's access right away
            //check if library is full
            libraryOccupancyQueue.isLibraryFull();

            // check If the date matches today...
            if (!proposedDateAndTime.toLocalDate().isEqual(LocalDate.now()))
                throw new ReservationNotForTodayException();

            //Check if Student is in currently in the library
            libraryOccupancyQueue.isUserPresentInLibrary(matricNumber);

            //Check if student already has multiple bookings for that day and if student has reached the maximum limit already.
            int numberOfBookings = multipleBookingsCheck(matricNumber, LocalDate.now(), CHECKED_OUT);
            maximumLimitCheck(numberOfBookings);

            //return an available seat number...
            return seatNumberResolver(libraryOccupancyQueue.getAvailableSeatNumberList());

        } else if (todayBooking) {
            LocalTime proposedStartTime = proposedDateAndTime.toLocalTime();
            LocalTime proposedEndTime = proposedStartTime.plusMinutes(duration.toMinutes());

            // check If the proposed booking date is for today
            if (!proposedDateAndTime.toLocalDate().isEqual(LocalDate.now()))
                throw new ReservationNotForTodayException();

            // Check if student already has multiple bookings for today and if student has reached the maximum limit already
            int numberOfBookings = multipleBookingsCheck(matricNumber, LocalDate.now(), CHECKED_OUT);
            maximumLimitCheck(numberOfBookings);

            //check the time the student is requesting for access...check if there are is an overlap...
            List<StudentReservation> bookedReservationForTodayList = studentReservationRepository.findByDateReservationWasMadeForAndReservationStatus(LocalDate.now(), BOOKED);

            var currentOccupancyList = libraryOccupancyQueue.fetchOccupancyQueueAsList().stream().map(user -> (StudentReservation) user.getReservation()).toList();

            //check the `bookedReservationForTodayList` list size. If the size is not up to greater or equal to the total number of seats
            //then it means that at least, there's a spot available...
            //this condition might be rarely triggered because it uses the size of the reservationList for today.
            if (bookedReservationForTodayList.size() < libraryConfigurationProperties.getNumberOfSeats()) {
                List<Long> availableSeatsList = LongStream.rangeClosed(1, libraryConfigurationProperties.getNumberOfSeats()).filter(seat -> bookedReservationForTodayList.stream().noneMatch(reservation -> reservation.getSeatNumber() == seat)).boxed().toList();
                return seatNumberResolver(availableSeatsList);
            }

            //since the number of bookings for that day is higher than total number of available seats,
            //then check if it's possible to find a spot without overlapping an existing reservation...
            var availableSeatNumberList = bookedReservationForTodayList.stream().filter(reservation -> checkForOverlap(proposedStartTime, reservation, proposedEndTime)).toList();
            if (!availableSeatNumberList.isEmpty())
                return seatNumberResolver(availableSeatNumberList.stream().mapToLong(Reservation::getSeatNumber).boxed().toList());

            //finally if it's none of the conditions above match, then check the current occupancy list for a spot that won't overlap...
            //since there might be a spot available since the search earlier was done using the reservation status as "BOOKED"
            availableSeatNumberList = currentOccupancyList.stream().filter(reservation -> checkForOverlap(proposedStartTime, reservation, proposedEndTime)).toList();

            if (!availableSeatNumberList.isEmpty())
                return seatNumberResolver(availableSeatNumberList.stream().mapToLong(Reservation::getSeatNumber).boxed().toList());

            throw new NoSpotAvailableException();
            //TODO maybe rather than throwing an exception,
            //TODO why not simple send a notification asking if he/she want's to be placed in a waiting queue pending when a spot opens up or maybe book for another time...
        } else {
            //meaning not today tomorrow and onwards...
            //make sure you verify it's an advanced date and not a past date...
            LocalTime proposedStartTime = proposedDateAndTime.toLocalTime();
            LocalTime proposedEndTime = proposedStartTime.plusMinutes(duration.toMinutes());

            if (proposedDateAndTime.toLocalDate().isBefore(LocalDate.now()) || proposedDateAndTime.toLocalDate().isEqual(LocalDate.now()))
                throw new AdvancedBookingRequiredException();

            //check if advanced bookings are permitted...
            if (!libraryConfigurationProperties.getAllowAdvancedBookings()) {
                throw new AdvancedBookingNotPermittedException();
            }

            //retrieve booking's for that day...
            List<StudentReservation> studentReservationList = studentReservationRepository.findByDateReservationWasMadeForAndReservationStatus(proposedDateAndTime.toLocalDate(), BOOKED).stream().toList();

            // Check if student already has multiple bookings for that day and if student has reached the maximum limit already.
            int numberOfBookings = multipleBookingsCheck(matricNumber, proposedDateAndTime.toLocalDate(), BOOKED);
            maximumLimitCheck(numberOfBookings);

            //check a reservation that does not overlap the existing reservation can be made ...
            List<StudentReservation> reservationList = studentReservationList.stream().filter(reservation -> checkForOverlap(proposedStartTime, reservation, proposedEndTime)).toList();

            if (!reservationList.isEmpty()) {
                List<Long> availableSeatList = LongStream.rangeClosed(1, libraryConfigurationProperties.getNumberOfSeats()).filter(seat -> reservationList.stream().noneMatch(reservation -> reservation.getSeatNumber() == seat)).boxed().toList();
                return seatNumberResolver(availableSeatList);
            }

            throw new NoSpotAvailableException();
            //TODO maybe rather than throwing an exception,
            //TODO why not simple send a notification asking if he/she want's to be placed in a waiting queue pending when a spot opens up or maybe book for another time...
        }
    }

    private int multipleBookingsCheck(String matricNumber, LocalDate localDate, ReservationStatus status) {
        int reservationCount = studentReservationRepository.findByStudentMatricNumberAndDateReservationWasMadeForAndReservationStatus(matricNumber, localDate, status);
        if (!libraryConfigurationProperties.getAllowMultipleBookings() && reservationCount >= 1)
            throw new MultipleBookingException();
        return reservationCount;
    }

    private void maximumLimitCheck(int reservationsCount) {
        if (libraryConfigurationProperties.getEnableLimitPerDay() && reservationsCount >= libraryConfigurationProperties.getMaximumLimitPerDay())
            throw new ReservationLimitExceededException(libraryConfigurationProperties.getMaximumLimitPerDay());
    }

    private boolean checkForOverlap(LocalTime proposedStartTime, StudentReservation studentReservation, LocalTime proposedEndTime) {
        LocalTime alreadyBookedStudentEstimatedEndTime = studentReservation.getCheckInTime().plusMinutes(studentReservation.getIntendedStay().toMinutes());

        //check if time extension is allowed and if the existing student reservation has asked for a time extension...
        if (libraryConfigurationProperties.getAllowTimeExtension() && studentReservation.isStayExtended())
            alreadyBookedStudentEstimatedEndTime = alreadyBookedStudentEstimatedEndTime.plusMinutes(studentReservation.getTotalExtensionDuration().toMinutes());

        return (!proposedStartTime.isBefore(alreadyBookedStudentEstimatedEndTime) || !proposedEndTime.isAfter(studentReservation.getCheckInTime())) && (!proposedStartTime.isBefore(studentReservation.getCheckInTime()) || !proposedEndTime.isAfter(alreadyBookedStudentEstimatedEndTime)) && (!proposedStartTime.isAfter(studentReservation.getCheckInTime()) || !proposedEndTime.isBefore(alreadyBookedStudentEstimatedEndTime));
    }

    private Long seatNumberResolver(List<Long> availableSeatsList) {
        if (libraryConfigurationProperties.getEnableSeatRandomization()) {
            int randomIndex = new Random().nextInt(availableSeatsList.size());
            return availableSeatsList.get(randomIndex);
        }
        return availableSeatsList.get(0);
    }
}