package dev.banji.LibReserve.exceptions;

public class ReservationDoesNotExistException extends LibraryRuntimeException {
    public ReservationDoesNotExistException() {
        super("StudentReservation not found");
    }

    public ReservationDoesNotExistException(String message) {
        super(message + " is not in session currently.");
    }
}
