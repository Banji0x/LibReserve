package dev.banji.LibReserve.exceptions;

public class InvalidReservationCodeException extends LibraryRuntimeException {
    private InvalidReservationCodeException(String message) {
        super(message);
    }

    public static InvalidReservationCodeException currentlyInUseException() {
        return new InvalidReservationCodeException("The provided reservation code is currently in use.");
    }

    public static InvalidReservationCodeException cancelledException() {
        return new InvalidReservationCodeException("Unfortunately,the reservation linked with this code was cancelled.");
    }

    public static InvalidReservationCodeException checkedOutException() {
        return new InvalidReservationCodeException("The provided reservation code has been used already.");
    }

    public static InvalidReservationCodeException expiredException() {
        return new InvalidReservationCodeException("Sorry, this reservation ha expired.");
    }

}
