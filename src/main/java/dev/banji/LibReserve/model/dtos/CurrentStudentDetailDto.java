package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.InmemoryUserDetailDto;
import dev.banji.LibReserve.model.StudentReservation;
import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentStudentDetailDto(Long seatNumber, String matricNumber,
                                      StudentReservation studentReservation, Jwt jwt) implements InmemoryUserDetailDto {
    @Override
    public Long getSeatNumber() {
        return seatNumber;
    }

    @Override
    public String getIdentifier() {
        return String.valueOf(matricNumber);
    }

    @Override
    public StudentReservation getReservation() {
        return studentReservation;
    }

    @Override
    public String getJwt() {
        return String.valueOf(jwt);
    }
}
