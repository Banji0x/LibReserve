package dev.banji.LibReserve.config.tokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class StudentAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal, credentials;

    private StudentAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(true); //this is important
    }

    private StudentAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);//this is important
    }

    public static StudentAuthenticationToken authenticatedToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new StudentAuthenticationToken(principal, credentials, authorities);
    }

    public static StudentAuthenticationToken unAuthenticatedToken(Object principal, Object credentials) {
        return new StudentAuthenticationToken(principal, credentials);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }
}
