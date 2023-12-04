package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.*;
import dev.banji.LibReserve.model.LibraryOccupancyQueue;
import dev.banji.LibReserve.model.Student;
import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import dev.banji.LibReserve.model.dtos.StudentReservationDto;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import dev.banji.LibReserve.repository.StudentRepository;
import dev.banji.LibReserve.repository.StudentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static dev.banji.LibReserve.model.enums.ReservationStatus.*;
import static java.time.LocalDate.now;

@Service
@RequiredArgsConstructor
public class LibrarianService {
    private final LibraryConfigurationProperties libraryConfigurationProperties;
    private final LibraryOccupancyQueue occupancyQueue;
    private final StudentReservationRepository studentReservationRepository;
    private final StudentRepository studentRepository;

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
    private Optional<StudentReservation> allowEntry(StudentReservation studentReservation) {
        if (!studentReservation.getReservationStatus().equals(BOOKED))
            throw new InvalidReservationException("Expired Reservation");
        validateEntryTime(studentReservation); //validate the entry time
        StudentReservation updatedReservationObject = signInStudent(studentReservation); //sign-in reservation...
        occupancyQueue.isLibraryFull(); //TODO normally this shouldn't throw any exception since the student already has a reservation
        boolean isCurrentlyInLibrary = occupancyQueue.isUserPresentInLibrary(studentReservation.getStudent().getMatricNumber()).isPresent(); //if the student is already in the library...
        boolean signedIn = occupancyQueue.signInStudent(new CurrentStudentDetailDto(studentReservation.getStudent().getMatricNumber(), updatedReservationObject));
        return (signedIn && !isCurrentlyInLibrary) ? Optional.of(updatedReservationObject) : Optional.empty();
    }

    private StudentReservation signInStudent(StudentReservation studentReservation) {
        studentReservation.setCheckInTime(LocalTime.now());
        return updateReservationStatus(studentReservation, CHECKED_IN);
    }


    // This method still needs some work.
    // Because it simply fetches one reservation from the repo randomly I think and that might not always be the best option.
    // Since the student might have multiple reservations for the day.
    public void validateStudentEntryByMatricNumber(String matricNumber) {

        StudentReservation studentReservation = studentReservationRepository.findByDateReservationWasMadeForAndStudentMatricNumber(now(), matricNumber).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });
        allowEntry(studentReservation).orElseThrow(() -> {
            throw new LibraryRuntimeException();
        });
    }

    public void validateStudentEntryByReservationCode(String reservationCode) {
        StudentReservation studentReservation = studentReservationRepository.findByReservationCodeAndDateReservationWasMadeFor(reservationCode, now()).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });
        allowEntry(studentReservation).orElseThrow(() -> {
            throw new LibraryRuntimeException();
        });
    }

    public void invalidateStudentSessionByReservationCode(String reservationCode) {
        StudentReservation studentReservation = occupancyQueue.isStudentPresentInLibrary(reservationCode).orElseThrow(() -> {
            throw new StudentNotInLibraryException();
        });
        invalidateStudentSession(studentReservation);
    }

    public void invalidateStudentSessionByMatricNumber(String matricNumber) {
        StudentReservation studentReservation = (StudentReservation) occupancyQueue.isUserPresentInLibrary(matricNumber).orElseThrow(() -> {
            throw new StudentNotInLibraryException();
        });
        invalidateStudentSession(studentReservation);
    }

    private void invalidateStudentSession(StudentReservation studentReservation) {
        studentReservation.setReservationStatus(BLACKLISTED);
        studentReservation.setCheckOutDateAndTime(LocalDateTime.now()); //check out user...
        studentReservationRepository.save(studentReservation);
        boolean reservationInvalidated = occupancyQueue.signOutStudent(new CurrentStudentDetailDto(studentReservation.getStudent().getMatricNumber(), studentReservation));
        if (!reservationInvalidated) throw new LibraryRuntimeException();
        //TODO notify the user via notifications that he should exit the library.
    }

    public void blacklistStudent(String matricNumber) {
        Student student = studentRepository.findByMatricNumber(matricNumber).orElseThrow(() -> {
            throw UserNotFoundException.StudentNotFoundException();
        });
        student.getAccount().setNotLocked(false);
        studentRepository.save(student);
        //TODO notify the user via notifications that he should exit the library. That's if he's still in the library...
    }

    private void validateEntryTime(StudentReservation studentReservation) {
        var bookedEntryTime = studentReservation.getTimeReservationWasMadeFor();
        var currentEntryTime = LocalTime.now();
        boolean isEarlyCheckInAllowed = libraryConfigurationProperties.getAllowEarlyCheckIn();
        boolean isLateCheckInAllowed = libraryConfigurationProperties.getAllowLateCheckIn();

        //the time Difference between the booked and the current time.
        Duration duration = Duration.between(bookedEntryTime, currentEntryTime);

        boolean durationNegative = duration.isNegative(); //meaning it's already a late check-in...
        long differenceInMinutes = Math.abs(duration.toMinutes()); // the total difference. this is a +ve value...


        if (durationNegative) {

            //this means the reservation is valid if it's not greater than recommended check in time.
            // or if late check-in is allowed, then if it's not greater than the late check in time.
            if (differenceInMinutes <= libraryConfigurationProperties.getRecommendedCheckInTime() || (differenceInMinutes <= libraryConfigurationProperties.getAllowedLateCheckInTimeInMinutes() && isLateCheckInAllowed)) {
                return; //valid entry
            }
            // anything below this line simply means it's a late check in...
            updateReservationStatus(studentReservation, EXPIRED); //TODO normally, this will be taken care of by the 'LibraryManagementService'
            throw new LateCheckInException();
//            if (!isLateCheckInAllowed || (differenceInMinutes > configProperties.getAllowedLateCheckInTimeInMinutes())) {
//                updateReservationStatus(studentReservation, EXPIRED); //TODO normally, this will be taken care of by the 'LibraryManagementService'
//                throw new LateCheckInException();
//            }
        }

        if ((!isEarlyCheckInAllowed) && differenceInMinutes > libraryConfigurationProperties.getRecommendedCheckInTime()) {
            //meaning it's an early check-in...
            throw new EarlyCheckInException(studentReservation.getTimeReservationWasMadeFor());
        }

        //anything below this line means it's a valid entry time...
    }

//    public boolean signOutLoggedInStudentByMatricNumber(String matricNumber) {
//        CurrentStudentDetailDto studentDetailDto = occupancyQueue.stream().filter(studentDetail -> studentDetail instanceof CurrentStudentDetailDto).map(studentDetail -> (CurrentStudentDetailDto) studentDetail).filter(studentDetail -> studentDetail.matricNumber().equals(matricNumber)).findFirst().orElseThrow(() -> {
//            throw new ReservationDoesNotExistException(matricNumber);
//        });
//        return signOutLoggedInStudent(studentDetailDto);
//    }
//
//    public boolean signOutLoggedInStudentByReservationCode(String reservationCode) {
//        var studentDetailDto = fetchLoggedInStudentDetail(reservationCode);
//        return signOutLoggedInStudent(studentDetailDto);//TODO notify the user via notifications that he no longer has a valid session.
//    }

//    public boolean signOutLoggedInStudentBySeatNumber(Long seatNumber) {
//        CurrentStudentDetailDto studentDetailDto = occupancyQueue.stream().filter(studentDetail -> studentDetail instanceof CurrentStudentDetailDto).map(studentDetail -> (CurrentStudentDetailDto) studentDetail).filter(studentDetail -> studentDetail.getSeatNumber().equals(seatNumber)).findFirst().orElseThrow(() -> {
//            throw new ReservationDoesNotExistException(); //thrown when a student does not have a session
//        });
//        return signOutLoggedInStudent(studentDetailDto);//TODO notify the user via notifications that he no longer has a valid session.
//    }

    /**
     * This method simply verifies if a reservation code
     *
     * @return a dto of type "StudentReservationDto"
     */
    public StudentReservationDto verifyStudentReservationCode(String reservationCode) {
        StudentReservation studentReservation = studentReservationRepository.findByReservationCode(reservationCode).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });
        return new StudentReservationDto(studentReservation);
    }

    /**
     * This method will fetch the student reservations for today
     */
    public List<StudentReservationDto> fetchStudentReservationForToday(String matricNumber) {
        List<StudentReservation> studentReservationList = studentReservationRepository.findByStudentMatricNumberAndDateReservationWasMadeFor(matricNumber, now());
        return mapToSimpleStudentReservationDtos(studentReservationList);
    }

    private List<StudentReservationDto> mapToSimpleStudentReservationDtos(List<StudentReservation> studentReservationList) {
        if (studentReservationList.isEmpty()) throw new ReservationDoesNotExistException();
        return studentReservationList
                .stream()
                .map(StudentReservationDto::new)
                .toList();
    }

//    public Boolean verifyReservationCodeAndSignInStudent(String reservationCode) {
//        var studentReservation = studentReservationRepository.findByReservationCodeAndReservationStatus(reservationCode, BOOKED).orElseThrow(() -> {
//            throw new ReservationDoesNotExistException();
//        });
//
//        //verify if it's for that day.
//        if (!studentReservation.getDateReservationWasMadeFor().equals(now()))
//            throw ReservationNotForTodayException.dateFormatter(studentReservation.getDateReservationWasMadeFor());
//
//        //verify the time...
//        LocalTime currentTime = LocalTime.now();
//        LocalTime bookedTime = studentReservation.getTimeReservationWasMadeFor();
//
//        //TODO implement an isOpen and isClose field in the config properties...
//
//        boolean lateCheckIn = currentTime.isAfter(bookedTime);
//
//        if (!configProperties.getAllowLateCheckIn() && lateCheckIn) {
//            updateReservationStatus(studentReservation, EXPIRED);
//            throw new BookingTimeHasElapsedException();
//        }
//
//        boolean hasExpired = false;
//
//        if (lateCheckIn) {
//            hasExpired = !bookedTime.plusMinutes(configProperties.getLateCheckInTime()).isAfter(currentTime);
//        }
//
//        if (hasExpired) {
//            //you can either invalidate the token here, or wait till the scheduler invalidates it...
//            updateReservationStatus(studentReservation, EXPIRED);
//            throw new BookingTimeHasElapsedException();
//        }
//
//        //this line downwards means his/her booking has not expired, but it doesn't mean he's checking in at the right time...
//        //Students can be allowed to check in minutes before their booked time.
//        boolean earlyCheckIn = currentTime.isBefore(bookedTime);
//
//        if (!configProperties.getAllowEarlyCheckIn() && earlyCheckIn) //check if student is allowed to check in early...
//            throw new EarlyCheckInException(studentReservation.getTimeReservationWasMadeFor());
//
//        if (currentTime.plusMinutes(configProperties.getAllowedEarlyCheckInMinutes()).isBefore(bookedTime)) //check if student is checking in at the right time...
//            throw new EarlyCheckInException(studentReservation.getTimeReservationWasMadeFor());
//
//        //update check in time...
//        studentReservation.setCheckInTime(LocalTime.now());
//        updateReservationStatus(studentReservation, CHECKED_IN);
//
//        //verify if the booking is for now...
//        boolean validDateAndTime = verifyBookingDateAndTime(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor());
//
//        //generate Jwt token
//
//        //add to the library queue...
////        return libraryOccupancyQueue.signInStudent(new CurrentStudentDetailDto(studentReservation.getSeatNumber(), studentReservation.getStudent().getMatricNumber(), studentReservation, jwt)) && validDateAndTime;
//        return null;
//    }

//    private boolean verifyBookingDateAndTime(LocalDate bookedDate, LocalTime bookedTime) {
//        LocalDateTime bookedDateAndTime = LocalDateTime.of(bookedDate, bookedTime);
//        LocalDateTime currentDateAndTime = LocalDateTime.now();
//        long timeDifferenceInMinutes = Duration.between(bookedDateAndTime, currentDateAndTime).toMinutes(); //TODO still needs to be checked...
//
//        if (!configProperties.getAllowEarlyCheckIn() && timeDifferenceInMinutes < 0)
//            throw new InvalidReservationException("Early check-in is not allowed for past bookings.");
//
//        if (!configProperties.getAllowLateCheckIn() && timeDifferenceInMinutes > configProperties.getLateCheckInTime())
//            throw new InvalidReservationException("Late check-in is not allowed for bookings beyond the allowed time frame.");
//        return true;
//    }

    private StudentReservation updateReservationStatus(StudentReservation studentReservation, ReservationStatus status) {
        studentReservation.setReservationStatus(status);
        return studentReservationRepository.save(studentReservation);
    }

    /**
     * This method will fetch all the student reservations
     */
    public List<StudentReservationDto> fetchAllStudentReservations(String matricNumber) {
        List<StudentReservation> studentReservationList = studentReservationRepository.findByStudentMatricNumber(matricNumber);
        return mapToSimpleStudentReservationDtos(studentReservationList);
    }
}