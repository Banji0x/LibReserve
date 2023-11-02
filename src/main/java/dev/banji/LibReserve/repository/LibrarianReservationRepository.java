package dev.banji.LibReserve.repository;

import dev.banji.LibReserve.model.LibrarianReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibrarianReservationRepository extends JpaRepository<LibrarianReservation, Long> {
    Optional<LibrarianReservation> findByLibrarianStaffNumber(String staffNumber);
}
