package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.InmemoryUserDetailDto;
import dev.banji.LibReserve.model.StudentReservation;

public record CurrentStudentDetailDto(String matricNumber,
                                      StudentReservation studentReservation) implements InmemoryUserDetailDto {

    public String getReservationCode() {
        return studentReservation.getReservationCode();
    }

    @Override
    public Long getSeatNumber() {
        return studentReservation.getSeatNumber();
    }

    @Override
    public String getIdentifier() {
        return String.valueOf(matricNumber);
    }

    @Override
    public StudentReservation getReservation() {
        return studentReservation;
    }
}
