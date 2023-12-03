package dev.banji.LibReserve.model.enums;

public enum ReservationStatus {
    CHECKED_IN,
    TIME_EXTENDED,
    WAITING,
    BOOKED,
    CANCELLED, //when the student cancels the reservation made..
    EXPIRED, //when a token has expired due to late check-in or time exhaustion.
    STUDENT_CHECKED_OUT,
    LIBRARIAN_CHECKED_OUT,
    SYSTEM_CHECKED_OUT,
    BLACKLISTED  //when the user was kicked out of the library

}