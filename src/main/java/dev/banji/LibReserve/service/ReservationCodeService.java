package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.ReservationDoesNotExistException;
import dev.banji.LibReserve.model.StudentReservation;
import dev.banji.LibReserve.repository.StudentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ReservationCodeService {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 5;
    private final StudentReservationRepository studentReservationRepository;
    private final LibraryConfigurationProperties libraryConfigurationProperties;

    public String generateNewReservationCode() {
        StringBuilder stringBuilder = new StringBuilder(TOKEN_LENGTH);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            stringBuilder.append(ALPHABET.charAt(index));
        }
        return stringBuilder.toString();
    }

    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
    public StudentReservation verifyReservationCode(String reservationCode, String matricNumber) {

        return studentReservationRepository.findByReservationCodeAndStudentMatricNumber(reservationCode, matricNumber).orElseThrow(() -> {
            throw new ReservationDoesNotExistException();
        });

    }


}
