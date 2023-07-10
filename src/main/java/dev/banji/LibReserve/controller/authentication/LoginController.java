package dev.banji.LibReserve.controller.authentication;

import dev.banji.LibReserve.service.TokenService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lib-reserve/token")
public class LoginController {
    private final TokenService tokenService;

    public LoginController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/librarian")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public String welcomeLibrarian(Authentication authentication) {
        return tokenService.generateJwt(authentication);
    }

    @PostMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String welcomeStudent(Authentication authentication) {
        return tokenService.generateJwt(authentication);
    }
}