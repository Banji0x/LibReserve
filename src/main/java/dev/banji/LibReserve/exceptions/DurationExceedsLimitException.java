package dev.banji.LibReserve.exceptions;

public class DurationExceedsLimitException extends TimeExtensionException {
    public DurationExceedsLimitException() {
        super("Sorry, the duration requested is higher than the maximum.");
    }
}
