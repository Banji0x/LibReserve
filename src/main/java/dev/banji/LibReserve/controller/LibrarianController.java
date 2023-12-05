package dev.banji.LibReserve.controller;

import dev.banji.LibReserve.model.dtos.StudentReservationDto;
import dev.banji.LibReserve.service.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/lib-reserve/librarian")
@RequiredArgsConstructor
public class LibrarianController {
    private final LibrarianService librarianService;

    @GetMapping("/validate/matricNumber")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public void validateStudentEntryByMatricNumber(String matricNumber) {
        librarianService.validateStudentEntryByMatricNumber(matricNumber);
    }

    @GetMapping("/validate/reservationCode")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public void validateStudentEntryByReservationCode(String reservationCode) {
        librarianService.validateStudentEntryByReservationCode(reservationCode);
    }

    @GetMapping("/invalidate/matricNumber")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public void invalidateStudentSessionByMatricNumber(String matricNumber) {
        librarianService.invalidateStudentSessionByMatricNumber(matricNumber);
    }

    @GetMapping("/invalidate/reservationCode")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public void invalidateStudentSessionByReservationCode(String reservationCode) {
        librarianService.invalidateStudentSessionByReservationCode(reservationCode);
    }

    @GetMapping("/blacklist")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public void blacklistStudent(String matricNumber) {
        librarianService.blacklistStudent(matricNumber);
    }

    @GetMapping("/verify/reservationCode")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public StudentReservationDto verifyStudentReservationCode(String reservationCode) {
        return librarianService.verifyStudentReservationCode(reservationCode);
    }

    @GetMapping("/reservations/today/{matricNumber}")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public List<StudentReservationDto> fetchStudentReservationForToday(@PathVariable String matricNumber) {
        return librarianService.fetchStudentReservationForToday(matricNumber);
    }

    @GetMapping("/reservations/all/{matricNumber}")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public List<StudentReservationDto> fetchAllStudentReservations(@PathVariable String matricNumber) {
        return librarianService.fetchAllStudentReservations(matricNumber);
    }

    @GetMapping("/reservations/now")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(OK)
    public List<StudentReservationDto> fetchCurrentStudentsInLibrary() {
        return librarianService.fetchCurrentStudentsInLibrary();
    }

    @GetMapping("/reservations/today")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(OK)
    public List<StudentReservationDto> fetchStudentListForToday() {
        return librarianService.fetchStudentListForToday();
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(OK)
    public void logout(JwtAuthenticationToken authentication) {
        librarianService.signOutLibrarian(authentication);
    }
}
