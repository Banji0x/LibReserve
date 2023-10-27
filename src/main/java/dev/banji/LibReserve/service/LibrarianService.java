package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.*;
import dev.banji.LibReserve.model.LibraryOccupancyQueue;
import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import dev.banji.LibReserve.repository.LibrarianRepository;
import dev.banji.LibReserve.repository.LibrarianReservationRepository;
import dev.banji.LibReserve.repository.StudentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static dev.banji.LibReserve.model.enums.ReservationStatus.*;

@Service
@RequiredArgsConstructor
public class LibrarianService {
    private final LibraryConfigurationProperties libraryConfigurationProperties;
    private final LibrarianRepository librarianRepository;
    private final LibrarianReservationRepository librarianReservationRepository;
    private final LibraryOccupancyQueue libraryOccupancyQueue;
    private final JwtTokenService jwtTokenService;
    private final StudentReservationRepository studentReservationRepository;
    private final ReservationCodeService reservationCodeService;

//    public Long retrieveNumberOfStudentsCurrentlyInTheLibrary() {
//        return null;
//    }
//
//    public Long retrieveNumberOfStudentsOnTheWaitingList() {
//        return null;
//    }
//
//    public Long retrieveNumberOfStudentsWithAdvanceBookings() {
//        return null;
//    }

    //    public Boolean addNewFaculties(Set<AllowedFaculties> allowedFacultiesList) {
//        return libraryConfigurationProperties.getSetOfAllowedFaculties().addAll(allowedFacultiesList);
//    }
//
//    public Boolean removeFaculties(List<AllowedFaculties> facultiesToBeRemoved) {
//        return libraryConfigurationProperties.getSetOfAllowedFaculties().removeAll(facultiesToBeRemoved);
//    }
//
//    public boolean signInLibrarian(String staffNumber) {
//        Librarian librarian = librarianRepository.findByStaffNumber(staffNumber)
//                .orElseThrow(() -> {
//                    throw UserNotFoundException.LibrarianNotFoundException();
//                });
//        try {
//            if (libraryOccupancyQueue.remainingCapacity() <= 0) //check if there's a spot available in the library
//                throw LibraryClosedException.LibraryMaximumLimitReached();
//        } catch (LibraryClosedException maximumLimitReachedException) {
//            if (!libraryConfigurationProperties.getReserveLibrarianSeat()) //since there's no spot, check if librarian's have a seat reserved by default.
//                throw new SeatReservationForLibrarianIsDisabled();
//        }
//
//        //seat number...
//        //I might have to randomization the seat numbers...
//        long seatNumber = 0; //TODO
//
//        LibrarianReservation librarianReservation = LibrarianReservation.builder().librarian(librarian).checkInTime(LocalTime.now()).checkOutTime(LocalTime.now()).reservationStatus(CHECKED_IN).reservationDate(LocalDate.now()).reservationTime(LocalTime.now()).seatNumber(seatNumber).dateReservationWasMadeFor(LocalDate.now()).timeReservationWasMadeFor(LocalTime.now()).build();
//        librarianReservationRepository.save(librarianReservation);
//        return true;
//    }

//    public boolean signOutLibrarian(Jwt jwt, String staffNumber) {
//        LibrarianReservation librarianReservation = librarianReservationRepository.findByStaffNumber(staffNumber).get();
//        librarianReservation.setCheckOutDateAndTime(LocalDateTime.now()); //check out user...
//        librarianReservation.setReservationStatus(CHECKED_OUT);//change librarianReservation status
//        librarianReservationRepository.save(librarianReservation);//update in repository
//
//        //TODO free up space ?
//        //maybe add seat to list...
//        boolean signedOut = libraryOccupancyQueue.signOutUser(new CurrentLibrarianDetailDto(librarianReservation.getSeatNumber(), staffNumber, librarianReservation, jwt));
//
//        //add JWT to blacklist
//        return jwtTokenService.blacklistJwt(String.valueOf(jwt)) && signedOut;
//    }


    public boolean signOutLoggedInStudentByMatricNumber(String matricNumber) {
        CurrentStudentDetailDto studentDetailDto = libraryOccupancyQueue.stream().filter(studentDetail -> studentDetail instanceof CurrentStudentDetailDto).map(studentDetail -> (CurrentStudentDetailDto) studentDetail).filter(studentDetail -> studentDetail.matricNumber().equals(matricNumber)).findFirst().orElseThrow(() -> {
            throw new ReservationDoesNotExistException(matricNumber);
        });
        return signOutLoggedInStudent(studentDetailDto);//TODO notify the user via notifications that he no longer has a valid session.
    }


    public boolean signOutLoggedInStudentByReservationCode(String reservationCode) {
        var studentDetailDto = fetchLoggedInStudentDetail(reservationCode);
        return signOutLoggedInStudent(studentDetailDto);//TODO notify the user via notifications that he no longer has a valid session.
    }

    public boolean signOutLoggedInStudentBySeatNumber(Long seatNumber) {
        CurrentStudentDetailDto studentDetailDto = libraryOccupancyQueue.stream().filter(studentDetail -> studentDetail instanceof CurrentStudentDetailDto).map(studentDetail -> (CurrentStudentDetailDto) studentDetail).filter(studentDetail -> studentDetail.getSeatNumber().equals(seatNumber)).findFirst().orElseThrow(() -> {
            throw new ReservationDoesNotExistException(); //thrown when a student does not have a session
        });
        return signOutLoggedInStudent(studentDetailDto);//TODO notify the user via notifications that he no longer has a valid session.
    }

    public ReservationStatus verifyLoggedInStudentReservationCode(String reservationCode) {
        var studentDetail = fetchLoggedInStudentDetail(reservationCode);
        return studentDetail.getReservation().getReservationStatus();
    }

    public Boolean verifyReservationCodeAndSignInStudent(String reservationCode) {
        var studentReservation = studentReservationRepository.findByReservationCodeAndReservationStatus(reservationCode, BOOKED).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });

        //verify if it's for that day.
        if (!studentReservation.getDateReservationWasMadeFor().equals(LocalDate.now()))
            throw ReservationNotForTodayException.dateFormatter(studentReservation.getDateReservationWasMadeFor());

        //verify the time...
        LocalTime currentTime = LocalTime.now();
        LocalTime bookedTime = studentReservation.getTimeReservationWasMadeFor();

        //TODO implement an isOpen and isClose field in the config properties...

        boolean lateCheckIn = currentTime.isAfter(bookedTime);

        if (!libraryConfigurationProperties.getAllowLateCheckIn() && lateCheckIn) {
            updateReservationStatus(studentReservation, EXPIRED);
            throw new BookingTimeHasElapsedException();
        }

        boolean hasExpired = false;

        if (lateCheckIn) {
            hasExpired = !bookedTime.plusMinutes(libraryConfigurationProperties.getAllowedTimeTillTokenExpirationInMinutes()).isAfter(currentTime);
        }

        if (hasExpired) {
            //you can either invalidate the token here, or wait till the scheduler invalidates it...
            updateReservationStatus(studentReservation, EXPIRED);
            throw new BookingTimeHasElapsedException();
        }

        //this line downwards means his/her booking has not expired, but it doesn't mean he's checking in at the right time...
        //Students can be allowed to check in minutes before their booked time.
        boolean earlyCheckIn = currentTime.isBefore(bookedTime);

        if (!libraryConfigurationProperties.getAllowEarlyCheckIn() && earlyCheckIn) //check if student is allowed to check in early...
            throw new EarlyCheckInException();

        if (currentTime.plusMinutes(libraryConfigurationProperties.getAllowedEarlyCheckInMinutes()).isBefore(bookedTime)) //check if student is checking in at the right time...
            throw new EarlyCheckInException();

        //update check in time...
        studentReservation.setCheckInTime(LocalTime.now());
        updateReservationStatus(studentReservation, CHECKED_IN);

        //verify if the booking is for now...
        boolean validDateAndTime = verifyBookingDateAndTime(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor());

        //generate Jwt token

        //add to the library queue...
//        return libraryOccupancyQueue.signInStudent(new CurrentStudentDetailDto(studentReservation.getSeatNumber(), studentReservation.getStudent().getMatricNumber(), studentReservation, jwt)) && validDateAndTime;
        return null;
    }

    private boolean verifyBookingDateAndTime(LocalDate bookedDate, LocalTime bookedTime) {
        LocalDateTime bookedDateAndTime = LocalDateTime.of(bookedDate, bookedTime);
        LocalDateTime currentDateAndTime = LocalDateTime.now();
        long timeDifferenceInMinutes = Duration.between(bookedDateAndTime, currentDateAndTime).toMinutes(); //TODO still needs to be checked...

        if (!libraryConfigurationProperties.getAllowEarlyCheckIn() && timeDifferenceInMinutes < 0)
            throw new InvalidReservationException("Early check-in is not allowed for past bookings.");

        if (!libraryConfigurationProperties.getAllowLateCheckIn() && timeDifferenceInMinutes > libraryConfigurationProperties.getAllowedTimeTillTokenExpirationInMinutes())
            throw new InvalidReservationException("Late check-in is not allowed for bookings beyond the allowed time frame.");
        return true;
    }

    private CurrentStudentDetailDto fetchLoggedInStudentDetail(String reservationCode) {

        return libraryOccupancyQueue.stream().filter(studentDetail -> studentDetail instanceof CurrentStudentDetailDto).map(studentDetail -> (CurrentStudentDetailDto) studentDetail).filter(studentDetail -> studentDetail.getReservation().getReservationCode().equals(reservationCode)).findFirst().orElseThrow(() -> {
            throw new ReservationDoesNotExistException(reservationCode); //thrown when a student does not have a session
        });
    }

    private boolean signOutLoggedInStudent(CurrentStudentDetailDto studentDetailDto) {
        StudentReservation studentReservation = studentDetailDto.getReservation();
        studentReservation.setReservationStatus(CHECKED_OUT);
        studentReservation.setCheckOutDateAndTime(LocalDateTime.now()); //check out user...
        //notify student TODO
        studentReservationRepository.save(studentReservation);
        //free up space in library queue...
        boolean spaceFreed = libraryOccupancyQueue.signOutUser(studentDetailDto);
        //blacklist JWT
        return spaceFreed && jwtTokenService.blacklistJwt(studentDetailDto.getJwt());
    }

    private void updateReservationStatus(StudentReservation studentReservation, ReservationStatus status) {
        studentReservation.setReservationStatus(status);
        studentReservationRepository.save(studentReservation);
    }
}
