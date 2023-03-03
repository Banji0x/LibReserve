package dev.banji.LibReserve.config.filters.tokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class LibrarianAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final Object credentials;

    private LibrarianAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    private LibrarianAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public static LibrarianAuthenticationToken authenticated(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new LibrarianAuthenticationToken(principal, credentials, authorities);
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

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }
}
