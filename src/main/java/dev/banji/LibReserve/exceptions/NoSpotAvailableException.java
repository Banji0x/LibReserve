package dev.banji.LibReserve.exceptions;

public class NoSpotAvailableException extends LibraryRuntimeException {
    public NoSpotAvailableException() {
        super("Sorry, the library has been fully booked for the stipulated time frame.");
    }
}
