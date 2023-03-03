package dev.banji.LibReserve.repository;

import dev.banji.LibReserve.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
    
}
