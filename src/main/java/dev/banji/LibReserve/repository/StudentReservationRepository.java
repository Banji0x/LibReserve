package dev.banji.LibReserve.repository;

import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentReservationRepository extends JpaRepository<StudentReservation, Long> {
    Optional<StudentReservation> findFirstByStudentMatricNumber(String matricNumber);

    List<StudentReservation> findByStudentMatricNumber(String matricNumber);

    List<StudentReservation> findByDateReservationWasMadeForAndReservationStatus(LocalDate reservationDate, ReservationStatus reservationStatus);

//    List<Reservation> findByDateReservationWasMadeForAndReservationStatus(LocalDate reservationDate, ReservationStatus reservationStatus);

    List<StudentReservation> findByReservationStatusAndStudentMatricNumber(ReservationStatus reservationStatus, String matricNumber);

    Optional<StudentReservation> findByReservationCodeAndStudentMatricNumber(String reservationCode, String matricNumber);

    List<StudentReservation> findByStudentMatricNumberAndReservationStatus(String matricNumber, ReservationStatus reservationStatus);

    List<StudentReservation> findByStudentMatricNumberAndDateReservationWasMadeFor(String matricNumber, LocalDate localDate);

    Optional<StudentReservation> findByDateReservationWasMadeForAndStudentMatricNumber(LocalDate localDate, String matricNumber);

    int countByStudentMatricNumberAndDateReservationWasMadeFor(String matricNumber, LocalDate localDate);

    Optional<StudentReservation> findByReservationCode(String reservationCode);

    Optional<StudentReservation> findByReservationCodeAndDateReservationWasMadeFor(String reservationCode, LocalDate now);

}