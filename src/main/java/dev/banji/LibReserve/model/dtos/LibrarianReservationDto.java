package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.enums.ReservationStatus;

import java.time.Duration;
import java.time.LocalDateTime;


public class LibrarianReservationDto extends AbstractReservationDto {
    protected LibrarianReservationDto(Long seatNumber, LocalDateTime reservationCreationDateTime, LocalDateTime intendedUsageDateTime, LocalDateTime checkOutDateAndTime, ReservationStatus reservationStatus, LocalDateTime checkInDateAndTime, Duration intendedStay) {
        super(seatNumber, reservationCreationDateTime, intendedUsageDateTime, checkOutDateAndTime, reservationStatus, checkInDateAndTime, intendedStay);
    }
}
