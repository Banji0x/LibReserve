package dev.banji.LibReserve.model;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentLibrarianDetailDto(String staffNumber, LibrarianReservation librarianReservation,
                                        Jwt jwt) implements InmemoryUserDetailDto {
    @Override
    public Long getSeatNumber() {
        return librarianReservation.seatNumber;
    }

    @Override
    public String getIdentifier() {
        return staffNumber;
    }

    @Override
    public Reservation getReservation() {
        return librarianReservation;
    }

}