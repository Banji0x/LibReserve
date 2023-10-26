package dev.banji.LibReserve.config.filters;

import dev.banji.LibReserve.config.tokens.StudentAuthenticationToken;
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
public class StudentAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationConverter studentAuthConverter;

    public StudentAuthenticationFilter(RequestMatcher studentJwtTokenPathRequestMatcher, AuthenticationConverter studentAuthConverter, AuthenticationManager providerManager, AuthenticationFailureHandler studentAuthenticationFailureHandler, AuthenticationSuccessHandler studentAuthenticationSuccessHandler) {
        super(studentJwtTokenPathRequestMatcher, providerManager);
        this.studentAuthConverter = studentAuthConverter;
        this.setAuthenticationFailureHandler(studentAuthenticationFailureHandler);
        this.setAuthenticationSuccessHandler(studentAuthenticationSuccessHandler);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response) && !isAlreadyAuthenticated(); //authenticate if the requestMatcher matches and the user has not being previously authenticated...
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        var unAuthenticatedStudentToken = (StudentAuthenticationToken) studentAuthConverter.convert(request);
        return getAuthenticationManager().authenticate(unAuthenticatedStudentToken);
    }

    protected boolean isAlreadyAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)
                && (SecurityContextHolder.getContext().getAuthentication() instanceof StudentAuthenticationToken);
    }
}
