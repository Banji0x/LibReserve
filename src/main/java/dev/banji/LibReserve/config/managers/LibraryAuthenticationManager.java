package dev.banji.LibReserve.config.managers;

import dev.banji.LibReserve.config.authenticationproviders.LibrarianAuthenticationProvider;
import dev.banji.LibReserve.config.authenticationproviders.StudentAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.stereotype.Component;

@Component
public class LibraryAuthenticationManager extends ProviderManager {
    public LibraryAuthenticationManager(LibrarianAuthenticationProvider librarianAuthenticationProvider, StudentAuthenticationProvider studentAuthenticationProvider) {
        super(studentAuthenticationProvider, librarianAuthenticationProvider);
    }
}