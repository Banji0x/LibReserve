package dev.banji.LibReserve.config.filters.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LibrarianAuthenticationFilter extends AuthenticationFilter {
    @Value("${university.config.librarian-login-url-post-only}")
    private boolean postOnly;
    private final RequestMatcher librarianLoginRequestMatcher;

    public LibrarianAuthenticationFilter(RequestMatcher librarianLoginRequestMatcher, AuthenticationConverter librarianAuthConverter, AuthenticationManager providerManager) {
        super(providerManager, librarianAuthConverter);
        this.setRequestMatcher(librarianLoginRequestMatcher);
        this.librarianLoginRequestMatcher = librarianLoginRequestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println(request.getServletPath() + " inside the library authentication...");
        if (librarianLoginRequestMatcher.matches(request))
            System.out.println(true);
        try {
            if (this.postOnly && !request.getMethod().equals("POST"))
                throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } catch (AuthenticationServiceException authenticationServiceException) {
            response.setStatus(405); //method not allowed
            response.getWriter().write(authenticationServiceException.getMessage());
            return;
        }
        Authentication authentication = getAuthenticationConverter().convert(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContext context = SecurityContextHolder
                .getContextHolderStrategy()
                .createEmptyContext();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !librarianLoginRequestMatcher.matches(request);
    }

}