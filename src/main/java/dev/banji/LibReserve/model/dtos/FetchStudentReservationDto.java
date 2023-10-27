package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.exceptions.LibraryRuntimeException;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

@SuppressWarnings("ConstantConditions")
public class FetchStudentReservationDto extends AbstractFetchReservationDto {
    private final String matricNumber;
    private final Boolean stayExtended;
    private Duration totalExtensionDuration;

    @Builder
    public FetchStudentReservationDto(Long seatNumber,
                                      LocalDateTime reservationCreationDateTime,
                                      LocalDateTime intendedUsageDateTime,
                                      LocalDateTime checkOutDateAndTime,
                                      ReservationStatus reservationStatus,
                                      String matricNumber,
                                      LocalDateTime checkInDateAndTime,
                                      Boolean stayExtended, Duration intendedStay, Duration totalExtensionDuration) {
        super(seatNumber, reservationCreationDateTime, intendedUsageDateTime, checkOutDateAndTime, reservationStatus, checkInDateAndTime, intendedStay);
        if (stayExtended.equals(false) && totalExtensionDuration != null)
            throw new LibraryRuntimeException(); //TODO write a more descriptive message..
        this.matricNumber = matricNumber;
        this.stayExtended = stayExtended;
        this.totalExtensionDuration = totalExtensionDuration;
    }
}