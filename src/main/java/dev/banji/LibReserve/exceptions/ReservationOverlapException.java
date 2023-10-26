package dev.banji.LibReserve.exceptions;

public class ReservationOverlapException extends RuntimeException {
    public ReservationOverlapException() {
        super("StudentReservation overlap detected.");
    }
}
