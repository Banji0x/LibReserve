package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.*;
import dev.banji.LibReserve.model.*;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import dev.banji.LibReserve.model.dtos.FetchStudentReservationDto;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import dev.banji.LibReserve.repository.StudentRepository;
import dev.banji.LibReserve.repository.StudentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static dev.banji.LibReserve.model.enums.ReservationStatus.*;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final LibraryConfigurationProperties libraryConfigurationProperties;
    private final StudentRepository studentRepository;
    private final StudentReservationRepository reservationRepository;
    private final ReservationCodeService reservationCodeService;
    private final LibraryOccupancyQueue libraryOccupancyQueue;
    private final JwtTokenService jwtTokenService;

    private String handleReservations(String matricNumber, Boolean isWalkInAccess, LocalDateTime proposedDateAndTime, Duration duration, Boolean isTodayBooking, Jwt jwt) {

        //check if there's the library is operational...
        if (!libraryConfigurationProperties.getAcceptingBookings())
            throw LibraryClosedException.LibraryNotOperationalException();

        //check if library has an available seat for the proposed time...
        Long availableSeat = internalReservationHandler(matricNumber, duration, isWalkInAccess, proposedDateAndTime, isTodayBooking);

        //check if the time is higher than permitted
        if (duration.toMinutes() > libraryConfigurationProperties.getBookingTimeAllowedInMinutes())
            throw new BookingTimeExceedsLimitException(libraryConfigurationProperties.getBookingTimeAllowedInMinutes());

        //generate reservationCode
        String generatedReservationCode = reservationCodeService.generateNewReservationCode();

        //the call to the student repository is to simply have access to the student object...
        Student student = studentRepository.findByMatricNumber(matricNumber).orElseThrow(() -> {
            throw UserNotFoundException.StudentNotFoundException();
        });

        //create studentReservation..
        StudentReservation studentReservation = StudentReservation
                .builder()
                .checkInTime(proposedDateAndTime.toLocalTime())
                .seatNumber(availableSeat)
                .intendedStay(duration)
                .reservationCreationDate(LocalDate.now())
                .reservationCreationTime(LocalTime.now())
                .dateReservationWasMadeFor(proposedDateAndTime.toLocalDate())
                .timeReservationWasMadeFor(proposedDateAndTime.toLocalTime())
                .reservationStatus(BOOKED)
                .student(student)
                .reservationCode(generatedReservationCode)
                .build();

        //persist to db...
        student.getStudentReservationList().add(studentReservation);
        studentRepository.save(student);

        // return the studentReservation code
        return generatedReservationCode;
    }

    public String reserveForTodayRequest(String matricNumber, LocalDateTime proposedDateAndTime, Duration duration, Jwt jwt) {
        return handleReservations(matricNumber, false, proposedDateAndTime, duration, true, jwt);
    }

    public String handleWalkInRequest(String matricNumber, Duration duration, Jwt jwt) {
        return handleReservations(matricNumber, true, LocalDateTime.now(), duration, false, jwt);
    }

    public String handleAdvancedRequest(String matricNumber, LocalDateTime proposedDateAndTime, Duration duration, Jwt jwt) {
        return handleReservations(matricNumber, false, proposedDateAndTime, duration, false, jwt);
    }

    public String requestForExtension(Authentication authentication, String matricNumber, Duration extensionDuration) {
        StudentReservation reservation = (StudentReservation) libraryOccupancyQueue.hasASession(matricNumber);
        if (!libraryConfigurationProperties.getAllowTimeExtension()) // if time extensionDuration is not allowed
            throw new TimeExtensionNotPermittedException();
        if (!libraryConfigurationProperties.getAllowMultipleTimeExtension())
            throw new MultipleTimeExtensionException();
        if (extensionDuration.toMinutes() > libraryConfigurationProperties
                .getMaximumTimeExtensionAllowedInMinutes()) //check the time duration
            throw new DurationExceedsLimitException();

        //update reservation...
        reservation.setStayExtended(true);
        Duration currentExtensionDuration = reservation.getTotalExtensionDuration();
        Duration totalDuration = currentExtensionDuration.plus(extensionDuration);

        reservation.setReservationStatus(TIME_EXTENDED);
        reservation.setTotalExtensionDuration(totalDuration);
        reservationRepository.save(reservation);

        //generate new Jwt token...
        return jwtTokenService.generateJwt(authentication, extensionDuration.toMinutes());
    }

    public boolean studentLogout(Jwt jwt, String matricNumber) {

        StudentReservation studentReservation = (StudentReservation) libraryOccupancyQueue.hasASession(matricNumber);
        studentReservation.setCheckOutDateAndTime(LocalDateTime.now()); //update time...
        studentReservation.setReservationStatus(CHECKED_OUT);//change studentReservation status
        reservationRepository.save(studentReservation);//update in repository

        //create dto...
        var currentStudentDetailDto = new CurrentStudentDetailDto(studentReservation.getSeatNumber(), matricNumber, studentReservation, jwt);

        //add JWT to blacklist
        boolean jwtBlackListed = jwtTokenService.blacklistJwt(String.valueOf(jwt));

        //free up seat...
        boolean seatFreedUp = libraryOccupancyQueue.signOutUser(currentStudentDetailDto);
        return jwtBlackListed && seatFreedUp;
    }

    public FetchStudentReservationDto retrieveLastReservation(String matricNumber) {
        Optional<StudentReservation> optionalStudentReservation = reservationRepository
                .findFirstByStudentMatricNumber(matricNumber);

        StudentReservation studentReservation = optionalStudentReservation.orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });

        return FetchStudentReservationDto
                .builder()
                .seatNumber(studentReservation.getSeatNumber())
                .reservationCreationDateTime(LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime()))
                .intendedUsageDateTime(LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor()))
                .intendedStay(studentReservation.getIntendedStay())
                .checkOutDateAndTime(studentReservation.getCheckOutDateAndTime())
                .reservationStatus(studentReservation.getReservationStatus())
                .matricNumber(matricNumber)
                .stayExtended(studentReservation.isStayExtended())
                .totalExtensionDuration(studentReservation.getTotalExtensionDuration())
                .build();

    }

    public List<FetchStudentReservationDto> fetchAllReservations(String matricNumber) {
        List<StudentReservation> reservationList = reservationRepository.findByStudentMatricNumber(matricNumber);
        if (reservationList.isEmpty()) throw new ReservationDoesNotExistException();
        return reservationList.stream().map(studentReservation ->
                FetchStudentReservationDto
                        .builder()
                        .seatNumber(studentReservation.getSeatNumber())
                        .reservationCreationDateTime(LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime()))
                        .intendedUsageDateTime(LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor()))
                        .intendedStay(studentReservation.getIntendedStay())
                        .checkOutDateAndTime(studentReservation.getCheckOutDateAndTime())
                        .reservationStatus(studentReservation.getReservationStatus())
                        .matricNumber(studentReservation.getStudent().getMatricNumber())
                        .stayExtended(studentReservation.isStayExtended())
                        .totalExtensionDuration(studentReservation.getTotalExtensionDuration())
                        .build()).toList();
    }

    public List<FetchStudentReservationDto> fetchReservationsByStatus(String matricNumber, ReservationStatus reservationStatus) {
        var reservationList = reservationRepository.findByReservationStatusAndStudentMatricNumber(reservationStatus, matricNumber);
        if (reservationList.isEmpty()) throw new ReservationDoesNotExistException();
        return reservationList.stream().map(studentReservation -> FetchStudentReservationDto
                .builder()
                .seatNumber(studentReservation.getSeatNumber())
                .reservationCreationDateTime(LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime()))
                .intendedUsageDateTime(LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor()))
                .intendedStay(studentReservation.getIntendedStay())
                .checkOutDateAndTime(studentReservation.getCheckOutDateAndTime())
                .reservationStatus(studentReservation.getReservationStatus())
                .matricNumber(matricNumber)
                .stayExtended(studentReservation.isStayExtended())
                .totalExtensionDuration(studentReservation.getTotalExtensionDuration())
                .build()).toList();
    }

    public boolean cancelAllReservations(String matricNumber) {
        List<StudentReservation> reservationList = reservationRepository.findByStudentMatricNumberAndReservationStatus(matricNumber, BOOKED);
        if (reservationList.isEmpty()) return false;
        reservationList.forEach(reservation -> {
            reservation.setReservationStatus(CANCELLED);
            reservationRepository.save(reservation);
        });
        reservationRepository.saveAll(reservationList);
        return true;
    }

    public boolean cancelReservationsByCode(String matricNumber, List<String> reservationCodesList) {
        reservationCodesList.forEach(reservationCode -> {
            cancelReservationByCode(reservationCode, matricNumber);
        });
        return true;
    }

    public boolean cancelLastReservation(String matricNumber) {
        StudentReservation reservation = reservationRepository.findByReservationStatusAndStudentMatricNumber(BOOKED, matricNumber).get(0);
        reservation.setReservationStatus(CANCELLED);
        reservationRepository.save(reservation);
        return true;
    }

    public boolean cancelReservationByCode(String reservationCode, String matricNumber) {
        StudentReservation reservation = reservationRepository.findByReservationCodeAndStudentMatricNumber(reservationCode, matricNumber).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });
        reservation.setReservationStatus(CANCELLED);
        reservationRepository.save(reservation);
        return true;
    }

    private Long internalReservationHandler(String matricNumber, Duration duration, Boolean walkInAccess, LocalDateTime proposedDateAndTime, Boolean todayBooking) {

        if (walkInAccess) { //TODO maybe implement an internal Reservation Resolver...
            //If student want's access right away

            //Check if Student is in currently in the library
            libraryOccupancyQueue.hasASession(matricNumber);

            //Check if student already has multiple bookings for that day and if student has reached the maximum limit already.
            int numberOfBookings = multipleBookingsCheck(matricNumber, LocalDate.now(), CHECKED_OUT);
            maximumLimitCheck(numberOfBookings);

            //check if there's a spot in the library right now, and return available seat number...
            return libraryOccupancyQueue.isLibraryFull();
        } else if (todayBooking) {
            LocalTime proposedStartTime = proposedDateAndTime.toLocalTime();
            LocalTime proposedEndTime = proposedStartTime.plusMinutes(duration.toMinutes());

            // check If the proposed booking date is for today
//            if (proposedDateAndTime.toLocalDate().isEqual(LocalDate.now()))
//                throw ReservationNotForTodayException.ReservationCannotBeForTodayException();

            // Check if student already has multiple bookings for today and if student has reached the maximum limit already
            int numberOfBookings = multipleBookingsCheck(matricNumber, LocalDate.now(), CHECKED_OUT);
            maximumLimitCheck(numberOfBookings);

            //check the time the student is requesting for access...check if there are is an overlap...
            List<StudentReservation> bookedReservationForTodayList = reservationRepository.findByDateReservationWasMadeForAndReservationStatus(LocalDate.now(), BOOKED);
            ArrayList<InmemoryUserDetailDto> occupancyQueueAsList = libraryOccupancyQueue.fetchOccupancyQueueAsList();

            var availableSeatNumberOptional = bookedReservationForTodayList.stream().filter(reservation -> !checkForOverlap(proposedStartTime, reservation, proposedEndTime)).map(Reservation::getSeatNumber).findFirst();

            if (availableSeatNumberOptional.isPresent()) return availableSeatNumberOptional.get();

            availableSeatNumberOptional = occupancyQueueAsList.stream().filter(studentDetailDto -> !checkForOverlap(proposedStartTime, (StudentReservation) studentDetailDto.getReservation(), proposedEndTime)).map(InmemoryUserDetailDto::getSeatNumber).findFirst();
            if (availableSeatNumberOptional.isPresent()) return availableSeatNumberOptional.get();
            throw new ReservationOverlapException();
        } else {
            //meaning not today tomorrow and onwards...
            //make sure you verify it's an advanced date and not a past date...
            if (proposedDateAndTime.toLocalDate().isBefore(LocalDate.now()) || proposedDateAndTime.toLocalDate().isEqual(LocalDate.now()))
                throw new AdvancedBookingRequiredException();

            //check if advanced bookings are permitted...
            if (!libraryConfigurationProperties.getAllowAdvancedBookings()) {
                throw new AdvancedBookingNotPermittedException();
            }

            //retrieve booking's for that day...
            List<StudentReservation> studentReservationList = reservationRepository.findByDateReservationWasMadeForAndReservationStatus(proposedDateAndTime.toLocalDate(), BOOKED).stream().toList();

            if (studentReservationList.size() >= libraryConfigurationProperties.getNumberOfSeats())
                throw LibraryClosedException.LibraryMaximumLimitReached();

            // Check if student already has multiple bookings for that day and if student has reached the maximum limit already.
            int numberOfBookings = multipleBookingsCheck(matricNumber, proposedDateAndTime.toLocalDate(), BOOKED);
            maximumLimitCheck(numberOfBookings);

            List<Long> takenSeatsList = studentReservationList.stream().map(Reservation::getSeatNumber).toList();

            return IntStream.rangeClosed(1, Math.toIntExact(libraryConfigurationProperties.getNumberOfSeats())).filter(takenSeat -> !takenSeatsList.contains(takenSeat)).boxed().map(availableSeat -> Long.valueOf(availableSeat)).findFirst().get();
        }
    }

    private void checkForMultipleAndMaximumLimits(String matricNumber, LocalDate localDate, ReservationStatus status) {
        int reservationsCount = reservationRepository.findByStudentMatricNumberAndMadeForDateAndReservationStatus(matricNumber, localDate, status);

        if (!libraryConfigurationProperties.getAllowMultipleBookings() && reservationsCount >= 1)
            throw new MultipleBookingException();

        if (libraryConfigurationProperties.getEnableLimitPerDay() && reservationsCount >= libraryConfigurationProperties.getMaximumLimitPerDay())
            throw new StudentReservationLimitExceededException(libraryConfigurationProperties.getMaximumLimitPerDay());
    }

    private int multipleBookingsCheck(String matricNumber, LocalDate localDate, ReservationStatus status) {
        int reservationCount = reservationRepository.findByStudentMatricNumberAndMadeForDateAndReservationStatus(matricNumber, localDate, status);
        if (!libraryConfigurationProperties.getAllowMultipleBookings() && reservationCount >= 1)
            throw new MultipleBookingException();
        return reservationCount;
    }

    private void maximumLimitCheck(int reservationsCount) {
        if (libraryConfigurationProperties.getEnableLimitPerDay() && reservationsCount >= libraryConfigurationProperties.getMaximumLimitPerDay())
            throw new StudentReservationLimitExceededException(libraryConfigurationProperties.getMaximumLimitPerDay());
    }

    private boolean checkForOverlap(LocalTime proposedStartTime, StudentReservation studentReservation, LocalTime proposedEndTime) {
        LocalTime studentEstimatedEndTime = studentReservation.getCheckInTime().plusMinutes(studentReservation.getIntendedStay().toMinutes());
        //check if time extension is allowed and if the existing student reservation has asked for a time extension...
        if (libraryConfigurationProperties.getAllowTimeExtension() && studentReservation.isStayExtended()) {
            studentEstimatedEndTime = studentEstimatedEndTime.plusMinutes(studentReservation.getTotalExtensionDuration().toMinutes());
        }

        return (proposedStartTime.isBefore(studentEstimatedEndTime) &&
                proposedEndTime.isAfter(studentReservation.getCheckInTime())) ||
                (proposedStartTime.isBefore(studentReservation.getCheckInTime())
                        && proposedEndTime.isAfter(studentEstimatedEndTime))
                || (proposedStartTime.isAfter(studentReservation.getCheckInTime())
                && proposedEndTime.isBefore(studentEstimatedEndTime));
    }

}