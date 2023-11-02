package dev.banji.LibReserve.exceptions;

public class StudentNotInLibraryException extends ReservationDoesNotExistException {
    public StudentNotInLibraryException() {
    }

    public StudentNotInLibraryException(String message) {
        super(message);
    }
}
