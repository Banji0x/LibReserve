package dev.banji.LibReserve.model;

import dev.banji.LibReserve.model.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@Entity
public class LibrarianReservation extends Reservation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "librarian_id")
    private Librarian librarian;

    @Builder
    public LibrarianReservation(LocalTime checkInTime, long seatNumber, Duration intendedStay, LocalDate reservationCreationDate, LocalTime reservationCreationTime, LocalDate dateReservationWasMadeFor, LocalTime timeReservationWasMadeFor, ReservationStatus reservationStatus, LocalDateTime checkOutDateAndTime, Librarian librarian) {
        super(checkInTime, seatNumber, intendedStay, reservationCreationDate, reservationCreationTime, dateReservationWasMadeFor, timeReservationWasMadeFor, reservationStatus, checkOutDateAndTime);
        this.librarian = librarian;
    }
}
