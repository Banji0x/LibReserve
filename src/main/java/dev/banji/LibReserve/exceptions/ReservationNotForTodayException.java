package dev.banji.LibReserve.exceptions;

public class ReservationNotForTodayException extends LibraryRuntimeException {
    public ReservationNotForTodayException() {
        super("Reservation is not valid for today.");
    }

}