package dev.banji.LibReserve.controller.authentication;

import dev.banji.LibReserve.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lib-reserve/token")
@RequiredArgsConstructor
public class JWTGeneratorController {
    private final JwtTokenService tokenService;

    @PostMapping("/librarian")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public String librarianToken(Authentication authentication) {
        return tokenService.generateJwt(authentication);
    }

    @PostMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentToken(Authentication authentication) {
        return tokenService.generateJwt(authentication);
    }
}