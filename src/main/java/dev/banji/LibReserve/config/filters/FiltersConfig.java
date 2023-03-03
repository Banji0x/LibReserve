package dev.banji.LibReserve.config.filters;

import dev.banji.LibReserve.config.authenticationprovider.LibrarianAuthenticationProvider;
import dev.banji.LibReserve.config.authenticationprovider.StudentAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

@Configuration
public class FiltersConfig {
    final
    LibrarianAuthenticationProvider librarianAuthenticationProvider;
    final
    StudentAuthenticationProvider studentAuthenticationProvider;

    public FiltersConfig(LibrarianAuthenticationProvider librarianAuthenticationProvider, StudentAuthenticationProvider studentAuthenticationProvider) {
        this.librarianAuthenticationProvider = librarianAuthenticationProvider;
        this.studentAuthenticationProvider = studentAuthenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(studentAuthenticationProvider, librarianAuthenticationProvider);
    }


}
