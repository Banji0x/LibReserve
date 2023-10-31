package dev.banji.LibReserve.exceptions;

public class ReservationOverlapException extends LibraryRuntimeException {
    public ReservationOverlapException() {
        super("StudentReservation overlap detected.");
    }
}
