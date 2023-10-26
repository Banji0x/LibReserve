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

    List<StudentReservation> findByDateReservationWasMadeForAndStudentMatricNumber(LocalDate madeForDate, String matricNumber);

    List<StudentReservation> findByDateReservationWasMadeForAndReservationStatus(LocalDate reservationDate, ReservationStatus reservationStatus);

//    List<Reservation> findByDateReservationWasMadeForAndReservationStatus(LocalDate reservationDate, ReservationStatus reservationStatus);

    List<StudentReservation> findByReservationStatusAndStudentMatricNumber(ReservationStatus reservationStatus, String matricNumber);

    Integer findByReservationStatusAndStudentMatricNumberAndDateReservationWasMadeFor(ReservationStatus reservationStatus, String matricNumber, LocalDate madeForDate);

    Optional<StudentReservation> findByReservationCodeAndStudentMatricNumberAndReservationStatus(String reservationCode, String matricNumber, ReservationStatus reservationStatus);

    Optional<StudentReservation> findByReservationCodeAndStudentMatricNumber(String reservationCode, String matricNumber);

    boolean findByReservationStatus(ReservationStatus reservationStatus);

    List<StudentReservation> findByStudentMatricNumberAndReservationStatus(String matricNumber, ReservationStatus reservationStatus);

    int findByStudentMatricNumberAndMadeForDate(String matricNumber, LocalDate localDate);

    int findByStudentMatricNumberAndMadeForDateAndReservationStatus(String matricNumber, LocalDate localDate, ReservationStatus status);

    Optional<StudentReservation> findByReservationCode(String reservationCode);

    Optional<StudentReservation> findByReservationCodeAndReservationStatus(String reservationCode, ReservationStatus booked);
}