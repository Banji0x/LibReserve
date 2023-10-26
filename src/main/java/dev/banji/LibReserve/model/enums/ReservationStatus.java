package dev.banji.LibReserve.model.enums;

public enum ReservationStatus {
    CHECKED_IN,
    TIME_EXTENDED,
    WAITING,
    BOOKED,
    CHECKED_OUT,
    CANCELLED,
    EXPIRED, //when a token has expired due to late check-in or time exhaustion.
}