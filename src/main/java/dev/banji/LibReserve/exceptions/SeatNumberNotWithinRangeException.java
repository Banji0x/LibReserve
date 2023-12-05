package dev.banji.LibReserve.exceptions;

public class SeatNumberNotWithinRangeException extends LibraryRuntimeException {
    public SeatNumberNotWithinRangeException() {
        super("Librarian seat number not within range");
    }
}
