package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.enums.ReservationStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public interface AbstractReservationDto {

    LocalDateTime reservedDateAndTime();

    ReservationStatus reservationStatus();

    Long seatNumber();

    Duration initialDuration();


    LocalDateTime checkOutDateTime();

}