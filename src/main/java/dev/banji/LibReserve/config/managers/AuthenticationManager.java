package dev.banji.LibReserve.config.managers;

import dev.banji.LibReserve.config.authenticationproviders.LibrarianAuthenticationProvider;
import dev.banji.LibReserve.config.authenticationproviders.StudentAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationManager extends ProviderManager {
    public AuthenticationManager(LibrarianAuthenticationProvider librarianAuthenticationProvider, StudentAuthenticationProvider studentAuthenticationProvider) {
        super(studentAuthenticationProvider, librarianAuthenticationProvider);
    }
}