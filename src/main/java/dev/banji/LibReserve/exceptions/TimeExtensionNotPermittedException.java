package dev.banji.LibReserve.exceptions;

public class TimeExtensionNotPermittedException extends TimeExtensionException {
    public TimeExtensionNotPermittedException() {
        super("Time extension is not allowed. Please contact the librarian. ");
    }
}
