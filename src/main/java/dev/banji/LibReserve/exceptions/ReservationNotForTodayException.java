package dev.banji.LibReserve.exceptions;

public class ReservationNotForTodayException extends LibraryRuntimeException {
    private ReservationNotForTodayException() {
        super("Reservation is not valid for today.");
    }

}