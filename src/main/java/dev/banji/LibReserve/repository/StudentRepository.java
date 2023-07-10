package dev.banji.LibReserve.repository;

import dev.banji.LibReserve.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByMatricNumber(String matricNumber);
}
