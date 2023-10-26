package dev.banji.LibReserve.model;

import dev.banji.LibReserve.model.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
public class StudentReservation extends Reservation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(updatable = false, nullable = false)
    private String reservationCode;

    @Column(updatable = false)
    private boolean stayExtended;
    @Column(updatable = false)
    private Duration totalExtensionDuration;

    @Builder
    public StudentReservation(LocalTime checkInTime, long seatNumber, Duration intendedStay, LocalDate reservationCreationDate, LocalTime reservationCreationTime, LocalDate dateReservationWasMadeFor, LocalTime timeReservationWasMadeFor, ReservationStatus reservationStatus, LocalDateTime checkOutDateAndTime, Long id, Student student, String reservationCode, Duration totalExtensionDuration, boolean stayExtended) {
        super(checkInTime, seatNumber, intendedStay, reservationCreationDate, reservationCreationTime, dateReservationWasMadeFor, timeReservationWasMadeFor, reservationStatus, checkOutDateAndTime);
        this.id = id;
        this.student = student;
        this.reservationCode = reservationCode;
        this.totalExtensionDuration = totalExtensionDuration;
        this.stayExtended = stayExtended;
    }
}