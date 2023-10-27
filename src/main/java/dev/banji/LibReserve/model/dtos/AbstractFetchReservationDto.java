package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.enums.ReservationStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
public abstract class AbstractFetchReservationDto {
    protected Long seatNumber;
    protected LocalDateTime reservationCreationDateTime;
    protected LocalDateTime checkInDateAndTime;
    protected LocalDateTime intendedUsageDateTime;
    protected Duration intendedStay;
    protected LocalDateTime checkOutDateAndTime;
    protected ReservationStatus reservationStatus;

    protected AbstractFetchReservationDto(Long seatNumber, LocalDateTime reservationCreationDateTime, LocalDateTime intendedUsageDateTime, LocalDateTime checkOutDateAndTime, ReservationStatus reservationStatus, LocalDateTime checkInDateAndTime, Duration intendedStay) {
        this.seatNumber = seatNumber;
        this.reservationCreationDateTime = reservationCreationDateTime;
        this.intendedUsageDateTime = intendedUsageDateTime;
        this.checkInDateAndTime = checkInDateAndTime;
        this.checkOutDateAndTime = checkOutDateAndTime;
        this.reservationStatus = reservationStatus;
        this.intendedStay = intendedStay;
    }
}