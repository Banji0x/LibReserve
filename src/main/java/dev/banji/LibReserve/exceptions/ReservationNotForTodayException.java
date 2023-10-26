package dev.banji.LibReserve.exceptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationNotForTodayException extends LibraryRuntimeException {
    private ReservationNotForTodayException(LocalDate reservedForDate) {
        super("Reservation is not valid. It is valid for the " + reservedForDate);
    }

    private ReservationNotForTodayException(String message) {
        super(message);
    }

    public static ReservationNotForTodayException dateFormatter(LocalDate reservedForDate) {
        var customFormatter = DateTimeFormatter.ofPattern("dd,MM,yyyy"); // "dd,MM,yyyy" format
        reservedForDate.format(customFormatter);
        return new ReservationNotForTodayException(reservedForDate);
    }

    public static ReservationNotForTodayException ReservationCannotBeForTodayException() {
        return new ReservationNotForTodayException("Proposed Booking is not for today");
    }
}
