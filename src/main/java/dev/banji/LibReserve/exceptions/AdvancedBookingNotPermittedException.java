package dev.banji.LibReserve.exceptions;

public class AdvancedBookingNotPermittedException extends LibraryRuntimeException {
    public AdvancedBookingNotPermittedException() {
        super("Advanced booking is not allowed.");
    }
}
