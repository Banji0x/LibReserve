package dev.banji.LibReserve.exceptions;

import lombok.Getter;

@Getter
public class LibraryClosedException extends LibraryRuntimeException {
    private LibraryClosedException(String message) {
        super(message);
    }

    public static LibraryClosedException LibraryNotOperationalException() {
        return new LibraryClosedException("Sorry, the library has closed for now.");
    }

    public static LibraryClosedException LibraryMaximumLimitReached() {
        return new LibraryClosedException("Sorry, the library has reached it's maximum limit. You'll be notified when a spot becomes available."); //you'll be notified...
    }
}
