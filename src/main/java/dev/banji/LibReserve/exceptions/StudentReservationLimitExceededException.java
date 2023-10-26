package dev.banji.LibReserve.exceptions;

public class StudentReservationLimitExceededException extends LibraryRuntimeException {
    public StudentReservationLimitExceededException(int limit) {
        super("You have reached the limit of " + limit + " reservations per day. Please try again tomorrow.");
    }

    public StudentReservationLimitExceededException() {
        super("You have already reached the limit of 1 reservation per day. Please try again tomorrow.");
    }
}
