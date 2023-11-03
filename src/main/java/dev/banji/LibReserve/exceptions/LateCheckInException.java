package dev.banji.LibReserve.exceptions;

public class LateCheckInException extends LibraryRuntimeException {
    public LateCheckInException() {
        super();
    }

    public LateCheckInException(String message) {
        super(message);
    }
}
