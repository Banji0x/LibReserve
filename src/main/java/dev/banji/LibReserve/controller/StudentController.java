package dev.banji.LibReserve.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lib-reserve/student")
public class StudentController {
    @GetMapping("/") //secured with oauth2
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public String homePage(JwtAuthenticationToken authentication) {
        return "homePage " + authentication.getName();
    }

    @GetMapping("/welcome") //secured with oauth2
    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    public String welcome(JwtAuthenticationToken authentication) {
        return "Welcome " + authentication.getName();
    }
}
