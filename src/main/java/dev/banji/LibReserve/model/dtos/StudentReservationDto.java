package dev.banji.LibReserve.model.dtos;

import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.model.enums.ReservationStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public final class StudentReservationDto implements AbstractReservationDto {
    private final StudentReservation studentReservation;

    public StudentReservationDto(StudentReservation studentReservation) {
        this.studentReservation = studentReservation;
    }

    String matricNumber() {
        return studentReservation.getStudent().getMatricNumber();
    }

    @Override
    public LocalDateTime checkOutDateTime() {
        return studentReservation.getCheckOutDateAndTime();
    }

    public Duration totalDuration() {
        return studentReservation.getTotalExtensionDuration();
    }

    public String reservationCode() {
        return studentReservation.getReservationCode();
    }

    @Override
    public ReservationStatus reservationStatus() {
        return studentReservation.getReservationStatus();
    }

    public LocalDateTime reservationMadeOn() {
        return LocalDateTime.of(studentReservation.getReservationCreationDate(), studentReservation.getReservationCreationTime());
    }

    @Override
    public LocalDateTime reservedDateAndTime() {
        return LocalDateTime.of(studentReservation.getDateReservationWasMadeFor(), studentReservation.getTimeReservationWasMadeFor());
    }

    @Override
    public Duration initialDuration() {
        return studentReservation.getIntendedStay();

    }

    public Boolean stayExtended() {
        return studentReservation.isStayExtended();
    }

    public Duration extensionAdded() {
        return studentReservation.getTotalExtensionDuration();
    }

    @Override
    public Long seatNumber() {
        return studentReservation.getSeatNumber();
    }

}
