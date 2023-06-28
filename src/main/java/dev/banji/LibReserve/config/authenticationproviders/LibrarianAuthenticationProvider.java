package dev.banji.LibReserve.config.authenticationprovider;
package dev.banji.LibReserve.config.authenticationproviders;

import dev.banji.LibReserve.config.filters.tokens.LibrarianAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LibrarianAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public LibrarianAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService adminUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = adminUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof LibrarianAuthenticationToken))
            return null;
        String emailAddress = (String) authentication.getPrincipal();
        String rawCredentials = (String) authentication.getCredentials();
        UserDetails librarian = userDetailsService.loadUserByUsername(emailAddress);
        if (!passwordEncoder.matches(rawCredentials, librarian.getPassword()))
            throw new BadCredentialsException("Credentials do not match");
        return LibrarianAuthenticationToken.authenticated(librarian.getUsername(), rawCredentials, librarian.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LibrarianAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
