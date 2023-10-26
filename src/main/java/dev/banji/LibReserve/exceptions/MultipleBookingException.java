package dev.banji.LibReserve.exceptions;

public class MultipleBookingException extends LibraryRuntimeException {
    public MultipleBookingException() {
        super("Multiple bookings are not allowed.");
    }
}
