package dev.banji.LibReserve.model;

public interface InmemoryUserDetailDto {
    Long getSeatNumber();

    String getIdentifier();

    Reservation getReservation();
}
