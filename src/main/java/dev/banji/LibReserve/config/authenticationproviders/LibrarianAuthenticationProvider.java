package dev.banji.LibReserve.config.authenticationproviders;

import dev.banji.LibReserve.config.tokens.LibrarianAuthenticationToken;
import dev.banji.LibReserve.config.userDetails.LibrarianSecurityDetails;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LibrarianAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService librarianUserDetailsService;

    public LibrarianAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService librarianUserDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.librarianUserDetailsService = librarianUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof LibrarianAuthenticationToken librarianAuthenticationToken)) //delegate the authentication to another provider...
            return null;
        if ((authentication.getPrincipal() == null || authentication.getCredentials() == null))
            throw new BadCredentialsException("Bad Credentials");
        String staffNumber = (String) librarianAuthenticationToken.getPrincipal();
        String rawCredentials = (String) librarianAuthenticationToken.getCredentials();
        var librarianUserDetails = (LibrarianSecurityDetails) librarianUserDetailsService.loadUserByUsername(staffNumber);
        if (!passwordEncoder.matches(rawCredentials, librarianUserDetails.getPassword()))
            throw new BadCredentialsException("Bad Credentials.");
        if (!librarianUserDetails.isEnabled())
            throw new DisabledException("Account is disabled.");
        if (!librarianUserDetails.isAccountNonLocked())
            throw new LockedException("Account is locked.");
        return LibrarianAuthenticationToken.authenticated(librarianUserDetails, librarianUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LibrarianAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
