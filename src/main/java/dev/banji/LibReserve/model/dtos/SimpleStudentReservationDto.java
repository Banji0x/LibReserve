package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.enums.ReservationStatus;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
public record SimpleStudentReservationDto(String reservationCode, String matricNumber,
                                          ReservationStatus reservationStatus, Duration duration, Long seatNumber,
                                          LocalDateTime reservationDateAndTime) {
}
