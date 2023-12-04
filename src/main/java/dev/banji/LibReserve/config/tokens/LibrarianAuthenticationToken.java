package dev.banji.LibReserve.config.tokens;

import dev.banji.LibReserve.config.userDetails.LibrarianSecurityDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class LibrarianAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private Object credentials;

    private LibrarianAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true); //override
        this.principal = principal;
    }

    private LibrarianAuthenticationToken(Object principal, Object credentials) {
        super(null);
        setAuthenticated(false);//override
        this.principal = principal;
        this.credentials = credentials;
    }

    public static LibrarianAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        ((LibrarianSecurityDetails) principal).eraseCredentials();
        return new LibrarianAuthenticationToken(principal, authorities);
    }

    public static LibrarianAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new LibrarianAuthenticationToken(principal, credentials);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getStaffNumber() {
        return ((LibrarianSecurityDetails) principal).getStaffNumber();
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }
}
