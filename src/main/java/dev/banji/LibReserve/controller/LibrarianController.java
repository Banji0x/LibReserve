package dev.banji.LibReserve.controller;

import dev.banji.LibReserve.model.dtos.SimpleStudentReservationDto;
import dev.banji.LibReserve.service.LibrarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("api/lib-reserve/librarian")
@RequiredArgsConstructor
public class LibrarianController {
    private final LibrarianService librarianService;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    public String homePage(JwtAuthenticationToken authentication) {
        return "homePage " + authentication.getName();
    }
//    @PostMapping("/json/addNewFaculty")
//    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
//    public Boolean addNewFaculties(@RequestBody String jsonData) throws JsonProcessingException {
//        Set<AllowedFaculties> newFaculties = objectMapper.readValue(jsonData, new TypeReference<>() {
//        });
//        return librarianService.addNewFaculties(newFaculties);
//        return null;
//    }
//    @PostMapping("/json/removeFaculty")
//    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
//    public Boolean removeFaculties(List<AllowedFaculties> removeFacultiesList) {
//        return librarianService.removeFaculties(removeFacultiesList);
//        return null;
//    }

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
    public SimpleStudentReservationDto verifyStudentReservationCode(String reservationCode) {
        return librarianService.verifyStudentReservationCode(reservationCode);
    }

    @GetMapping("/reservations/today")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public List<SimpleStudentReservationDto> fetchStudentReservationForToday(String matricNumber) {
        return librarianService.fetchStudentReservationForToday(matricNumber);
    }

    @GetMapping("/reservations/all")
    @PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')") //secured with oauth2
    @ResponseStatus(ACCEPTED)
    public List<SimpleStudentReservationDto> fetchAllStudentReservations(String matricNumber) {
        return librarianService.fetchAllStudentReservations(matricNumber);
    }
}
