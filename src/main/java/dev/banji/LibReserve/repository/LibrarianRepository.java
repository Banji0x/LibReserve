package dev.banji.LibReserve.repository;

import dev.banji.LibReserve.exceptions.UserNotFoundException;
import dev.banji.LibReserve.model.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    Optional<Librarian> findByEmailAddress(String emailAddress) throws UserNotFoundException;
}
