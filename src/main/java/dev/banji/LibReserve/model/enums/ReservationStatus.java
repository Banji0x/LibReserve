package dev.banji.LibReserve.model.enums;

public enum ReservationStatus {
    CHECKED_IN,
    TIME_EXTENDED,
    WAITING,
    BOOKED,
    CHECKED_OUT, // when the student checked out himself...
    CANCELLED, //when the student cancels the reservation...
    EXPIRED, //when a token has expired due to late check-in or time exhaustion.
    BLACKLISTED //when the user was kicked out of the library
}
