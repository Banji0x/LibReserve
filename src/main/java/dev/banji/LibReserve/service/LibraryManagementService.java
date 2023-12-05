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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.banji.LibReserve.model.enums.ReservationStatus.*;
import static java.lang.Math.abs;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class LibraryManagementService {
    private final StudentReservationRepository studentReservationRepository;
    private final LibraryOccupancyQueue libraryOccupancyQueue;

    private final LibraryConfigurationProperties libraryConfigurationProperties;
    private final NotificationService notificationService;

    // check for students that have exhausted the time allocated.
    @Scheduled(fixedRate = 1, timeUnit = MINUTES)
    public void automaticStudentSignOutService() {
        if (libraryOccupancyQueue.isEmpty()) return; //no student is currently in the library ...

        libraryOccupancyQueue.stream().filter(inmemoryUser -> inmemoryUser instanceof CurrentStudentDetailDto) //this method is for students alone.
                .map(inmemoryUserDetail -> (CurrentStudentDetailDto) inmemoryUserDetail).forEach(studentDetailDto -> {
                    StudentReservation studentReservation = studentDetailDto.getReservation();
                    LocalTime studentCheckInTime = studentReservation.getCheckInTime();
                    LocalTime intendedCheckOutTime = studentCheckInTime.plusMinutes(studentReservation.getIntendedStay().toMinutes());
                    intendedCheckOutTime = studentReservation.isStayExtended() ? intendedCheckOutTime.plusMinutes(studentReservation.getTotalExtensionDuration().toMinutes()) : intendedCheckOutTime;

                    if (intendedCheckOutTime.isAfter(LocalTime.now())) //this means that the student session is still valid...
                    {
                        if (!libraryConfigurationProperties.getSendNotifications().enabled()) return;

                        //Send notifications
                        //
                        long remainingTimeInMinutes = abs(ChronoUnit.MINUTES.between(studentCheckInTime, intendedCheckOutTime));
                        var notificationList = libraryConfigurationProperties.getNotificationList();

                        notificationList.forEach(notification -> {
                            if (notification.timeLeft().equals(remainingTimeInMinutes))
                                notificationService.notifyStudent(studentReservation.getStudent().getMatricNumber(), new SingleWebNotificationDto(studentReservation.getStudent().getMatricNumber(), notification.message(), notification.timeLeft()));
                        });
                        return;
                    }

                    //update the studentReservation to "SERVICE_CHECKED_OUT" since the allocated time has being exhausted...
                    studentReservation.setReservationStatus(SYSTEM_CHECKED_OUT);
                    studentReservationRepository.save(studentReservation);

                    //remove from the queue
                    boolean removed = libraryOccupancyQueue.signOutStudent(studentDetailDto);
                    if (!removed) throw new LibraryRuntimeException();
                });
    }

    @Scheduled(fixedRate = 1, timeUnit = MINUTES)
    // check for reservations that needs to be updated to "EXPIRED" every one minute...
    // I need to implement a service that invalidates reservations esp when their time has passed
    public void invalidateReservationService() {
        List<StudentReservation> bookedReservationListForToday = studentReservationRepository.findByDateReservationWasMadeForAndReservationStatus(LocalDate.now(), BOOKED);

        //if empty
        if (bookedReservationListForToday.isEmpty()) return;

        //this only checks and invalidates bookings for today. Bookings for tomorrow will be invalidated tomorrow...
        bookedReservationListForToday.forEach(reservation -> {
            if (libraryConfigurationProperties.getAllowLateCheckIn()) {
                if (reservation.getTimeReservationWasMadeFor().plusMinutes(libraryConfigurationProperties.getMaximumTimeExtensionAllowedInMinutes()).isAfter(LocalTime.now()))
                    return; //meaning this reservation is still valid.
            } else if (!reservation.getTimeReservationWasMadeFor().isAfter(LocalTime.now())) return;
            reservation.setReservationStatus(EXPIRED); //meaning reservation is now invalid.
            studentReservationRepository.save(reservation);
        });
    }

}