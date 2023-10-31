package dev.banji.LibReserve.exceptions;

public class ReservationLimitExceededException extends LibraryRuntimeException {
    public ReservationLimitExceededException(int limit) {
        super("You have reached the limit of " + limit + " reservations per day. Please try again tomorrow.");
    }

    public ReservationLimitExceededException() {
        super("You have already reached the limit of 1 reservation per day. Please try again tomorrow.");
    }
}
