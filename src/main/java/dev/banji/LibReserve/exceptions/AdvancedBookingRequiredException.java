package dev.banji.LibReserve.exceptions;

public class AdvancedBookingRequiredException extends LibraryRuntimeException {
    public AdvancedBookingRequiredException() {
        super("Advanced booking is required for this time.");
    }
}
