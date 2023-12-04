package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.LibrarianReservation;
import dev.banji.LibReserve.model.enums.ReservationStatus;

import java.time.Duration;
import java.time.LocalDateTime;


public record LibrarianReservationDto(LibrarianReservation librarianReservation) implements AbstractReservationDto {

    @Override
    public LocalDateTime checkOutDateTime() {
        return librarianReservation.getCheckOutDateAndTime();
    }

    @Override
    public ReservationStatus reservationStatus() {
        return librarianReservation.getReservationStatus();
    }

    @Override
    public LocalDateTime reservedDateAndTime() {
        return LocalDateTime.of(librarianReservation.getDateReservationWasMadeFor(), librarianReservation.getTimeReservationWasMadeFor());
    }

    @Override
    public Duration initialDuration() {
        return librarianReservation.getIntendedStay();

    }

    @Override
    public Long seatNumber() {
        return librarianReservation.getSeatNumber();
    }
}
