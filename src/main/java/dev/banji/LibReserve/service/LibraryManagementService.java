package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.LibraryRuntimeException;
import dev.banji.LibReserve.model.LibraryOccupancyQueue;
import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import dev.banji.LibReserve.repository.StudentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static dev.banji.LibReserve.model.enums.ReservationStatus.*;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class LibraryManagementService {
    private final StudentReservationRepository studentReservationRepository;
    private final LibraryOccupancyQueue libraryOccupancyQueue;

    private final LibraryConfigurationProperties libraryConfigurationProperties;

    // check for students that have exhausted the time allocated.
    @Scheduled(fixedRate = 1, timeUnit = MINUTES)
    public void automaticStudentSignOutService() {
        if (libraryOccupancyQueue.isEmpty())
            return; //no students in currently in the library ...

        libraryOccupancyQueue
                .stream()
                .filter(inmemoryUser -> inmemoryUser instanceof CurrentStudentDetailDto)
                .map(inmemoryUserDetail -> (CurrentStudentDetailDto) inmemoryUserDetail)
                .forEach(studentDetailDto -> {
                    StudentReservation studentReservation = studentDetailDto.getReservation();

                    LocalTime intendedStartTIme = studentReservation.getCheckInTime();
                    LocalTime intendedEndTime = intendedStartTIme.plusMinutes(studentReservation.getIntendedStay().toMinutes());
                    intendedEndTime = studentReservation.isStayExtended() ? intendedEndTime.plusMinutes(studentReservation.getTotalExtensionDuration().toMinutes()) : intendedEndTime;

                    if (intendedEndTime.isAfter(LocalTime.now())) //meaning time has not being used up...
                    {
                        notificationService(0, false); //TODO the time difference should be the remaining time in minutes...
                        return;
                    }

                    //update the studentReservation to "CHECKED_OUT" since the allocated time has being exhausted...
                    studentReservation.setReservationStatus(CHECKED_OUT);
                    studentReservationRepository.save(studentReservation);

                    //remove from the queue
                    boolean removed = libraryOccupancyQueue.signOutUser(studentDetailDto);
                    if (!removed) throw new LibraryRuntimeException();
                });
    }

    @Scheduled(fixedRate = 1, timeUnit = MINUTES)
    // check for reservations that needs to be updated to "EXPIRED" every one minute...
    // I need to implement a service that invalidates reservations esp when their time has passed
    public void invalidateReservedBookingsService() {
        List<StudentReservation> bookedReservationList = studentReservationRepository.findByDateReservationWasMadeForAndReservationStatus(LocalDate.now(), BOOKED);

        //if empty
        if (bookedReservationList.isEmpty())
            return;

        //this only checks and invalidates bookings for today. Bookings for tomorrow will be invalidated tomorrow...
        bookedReservationList.forEach(reservation -> {
            if (libraryConfigurationProperties.getAllowLateCheckIn()) {
                if (reservation.getTimeReservationWasMadeFor().plusMinutes(libraryConfigurationProperties.getMaximumTimeExtensionAllowedInMinutes()).isAfter(LocalTime.now())) {
                    return; //meaning it's still valid
                }
            } else if (!reservation.getTimeReservationWasMadeFor().isAfter(LocalTime.now()))
                return;
            reservation.setReservationStatus(EXPIRED); //meaning booking is now invalid.
            studentReservationRepository.save(reservation);
        });
    }

    //notification Service when time is almost up...
    //immediately notification to all students...
    private void notificationService(int timeLeft, boolean immediately) {
// Send notifications out. {probably to the Librarian and Student...} //TODO notifications Service
        if (!libraryConfigurationProperties.getSendNotifications()) return;
        //alert users
        if (immediately) {

        }
        List<Integer> notificationRolloutTimeList = libraryConfigurationProperties.getNotificationTimeListInMinutes();
        List<Integer> list = notificationRolloutTimeList.stream().filter(notificationTime -> notificationTime.equals(timeLeft)).toList();
        if (list.isEmpty()) return;

        //send out notifications
        //write logic here #TODO
    }

}