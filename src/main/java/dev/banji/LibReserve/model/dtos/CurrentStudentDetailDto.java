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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentStudentDetailDto that = (CurrentStudentDetailDto) o;

        if (!matricNumber.equals(that.matricNumber)) return false;
        return studentReservation.equals(that.studentReservation);
    }

    @Override
    public int hashCode() {
        int result = matricNumber.hashCode();
        result = 31 * result + studentReservation.hashCode();
        return result;
    }
}
