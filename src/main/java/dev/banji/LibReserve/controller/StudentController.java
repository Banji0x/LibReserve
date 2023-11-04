package dev.banji.LibReserve.controller;

import dev.banji.LibReserve.model.dtos.StudentReservationDto;
import dev.banji.LibReserve.model.enums.ReservationStatus;
import dev.banji.LibReserve.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/lib-reserve/student")
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/reservation/last")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public StudentReservationDto getLastReservation(Authentication authentication) {
        return studentService.retrieveLastReservation((String) authentication.getPrincipal());
    }


    @GetMapping("/reservation/{status}")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public List<StudentReservationDto> fetchReservationsBasedOnStatus(@PathVariable ReservationStatus status, Authentication authentication) {
        return studentService.fetchReservationsByStatus((String) authentication.getPrincipal(), status);
    }

    @GetMapping("/reservation/all")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public List<StudentReservationDto> fetchAllReservations(Authentication authentication) {
        return studentService.fetchAllReservations((String) authentication.getPrincipal());
    }

    @PostMapping("/reservation/today/now")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public String walkInRequest(JwtAuthenticationToken authentication, @RequestBody Duration duration) {
        return studentService.handleWalkInRequest((String) authentication.getPrincipal(), duration);
    }

    @PostMapping("/reservation/today")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public String reserveForLaterToday(JwtAuthenticationToken authentication, @RequestBody LocalDateTime proposedDateAndTIme, @RequestBody Duration duration) {
        return studentService.reserveForTodayRequest((String) authentication.getPrincipal(), proposedDateAndTIme, duration);
    }

    @PostMapping("/reservation/advance")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public String bookReservationsInAdvance(JwtAuthenticationToken authentication, @RequestBody LocalDateTime proposedDateAndTIme, @RequestBody Duration duration) {
        return studentService.handleAdvancedRequest((String) authentication.getPrincipal(), proposedDateAndTIme, duration);
    }

    @PostMapping("/reservation/extension")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public Boolean reservationExtension(JwtAuthenticationToken authentication, @RequestBody Duration duration) {
        return studentService.requestForExtension((String) authentication.getPrincipal(), duration);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public boolean logout(JwtAuthenticationToken authentication) {
        return studentService.studentLogout(authentication.getToken(), (String) authentication.getPrincipal());
    }

    @PostMapping("/reservation/cancel")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public boolean cancelLastReservation(Authentication authentication) {
        String matricNumber = (String) authentication.getPrincipal();
        return studentService.cancelLastReservation(matricNumber);
    }

    @PostMapping("/reservation/cancel/{reservationCode}")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public boolean cancelReservationByCode(Authentication authentication, @PathVariable String reservationCode) { //this cancels a reservation booked by the student using the reservation code.
        String matricNumber = (String) authentication.getPrincipal();
        return studentService.cancelReservationByCode(reservationCode, matricNumber);
    }

    @PostMapping("/reservations/cancel")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public boolean cancelReservationsByCode(Authentication authentication, List<String> reservationCodesList) { //this cancels a reservation booked by the student using the reservation code.
        String matricNumber = (String) authentication.getPrincipal();
        return studentService.cancelReservationsByCode(matricNumber, reservationCodesList);
    }

    @PostMapping("/reservations/cancel/all")
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public boolean cancelAllReservations(Authentication authentication) { //cancels all the reservations made by student.
        String matricNumber = (String) authentication.getPrincipal();
        return studentService.cancelAllReservations(matricNumber);
    }

}