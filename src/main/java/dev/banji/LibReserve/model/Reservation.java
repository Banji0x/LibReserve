package dev.banji.LibReserve.model;

import dev.banji.LibReserve.model.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * An abstract class representing a reservation in a library.
 */
@MappedSuperclass
@SuperBuilder
@Getter
@Setter
public abstract class Reservation {

    /**
     * The precise time the student checked in.
     */
    @Column(updatable = false, nullable = false)
    protected LocalTime checkInTime;

    /**
     * The seat number that was allocated.
     */
    @Column(updatable = false, nullable = false)
    protected long seatNumber;

    /**
     * The intended duration of the library stay in minutes.
     */
    @Column(updatable = false, nullable = false)
    private Duration intendedStay;

    /**
     * The date the reservation was created.
     */
    @Column(updatable = false, nullable = false)
    protected LocalDate reservationCreationDate;

    /**
     * The time the reservation was created.
     */
    @Column(updatable = false, nullable = false)
    protected LocalTime reservationCreationTime;

    /**
     * The date the reservation was made for.
     */
    @Column(updatable = false, nullable = false)
    protected LocalDate dateReservationWasMadeFor;

    /**
     * The time the reservation was made for.
     */
    @Column(updatable = false, nullable = false)
    protected LocalTime timeReservationWasMadeFor;


    /**
     * The reservation status.
     * For the librarian, this can only be "checked in" or "checked out".
     */
    protected ReservationStatus reservationStatus;

    /**
     * The checkout date and time.
     */
    protected LocalDateTime checkOutDateAndTime;

    public Reservation(LocalTime checkInTime, long seatNumber, Duration intendedStay, LocalDate reservationCreationDate, LocalTime reservationCreationTime, LocalDate dateReservationWasMadeFor, LocalTime timeReservationWasMadeFor, ReservationStatus reservationStatus, LocalDateTime checkOutDateAndTime) {
        this.checkInTime = checkInTime;
        this.seatNumber = seatNumber;
        this.intendedStay = intendedStay;
        this.reservationCreationDate = reservationCreationDate;
        this.reservationCreationTime = reservationCreationTime;
        this.dateReservationWasMadeFor = dateReservationWasMadeFor;
        this.timeReservationWasMadeFor = timeReservationWasMadeFor;
        this.reservationStatus = reservationStatus;
        this.checkOutDateAndTime = checkOutDateAndTime;
    }
}
