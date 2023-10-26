package dev.banji.LibReserve.exceptions;

public class BookingTimeExceedsLimitException extends LibraryRuntimeException {

    public BookingTimeExceedsLimitException(String allowedTime) {
        super("The maximum time allowed per student is " + allowedTime + " minutes.");
    }

    public BookingTimeExceedsLimitException(Long allowedTime) {
        super("The maximum time allowed per student is " + allowedTime + " minutes.");
    }
}
