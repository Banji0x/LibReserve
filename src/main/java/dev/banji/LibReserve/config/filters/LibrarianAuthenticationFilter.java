package dev.banji.LibReserve.config.filters;

import dev.banji.LibReserve.config.tokens.LibrarianAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class LibrarianAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationConverter librarianAuthConverter;

    public LibrarianAuthenticationFilter(RequestMatcher librarianJwtTokenPathRequestMatcher,
                                         AuthenticationConverter librarianAuthConverter,
                                         AuthenticationManager providerManager,
                                         AuthenticationFailureHandler librarianAuthenticationFailureHandler,
                                         AuthenticationSuccessHandler librarianAuthenticationSuccessHandler) {
        super(librarianJwtTokenPathRequestMatcher, providerManager);
        this.librarianAuthConverter = librarianAuthConverter;
        this.setAuthenticationFailureHandler(librarianAuthenticationFailureHandler);
        this.setAuthenticationSuccessHandler(librarianAuthenticationSuccessHandler);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response) && !isAlreadyAuthenticated(); //authenticate if the requestMatcher matches and the user has not being previously authenticated...
    }

    protected boolean isAlreadyAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)
                && (SecurityContextHolder.getContext().getAuthentication() instanceof LibrarianAuthenticationToken);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        var unAuthenticatedLibrarianToken = (LibrarianAuthenticationToken) librarianAuthConverter.convert(request);
        return getAuthenticationManager().authenticate(unAuthenticatedLibrarianToken);
    }
}