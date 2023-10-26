package dev.banji.LibReserve.exceptions;

public class LibraryRuntimeException extends RuntimeException {
    public LibraryRuntimeException(String message) {
        super(message);
    }

    public LibraryRuntimeException() {
        super("internal Server Error.");
    }
}
