package dev.banji.LibReserve.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAccessTokenBlacklistAuthenticationFilter extends OncePerRequestFilter {
    private final List<Jwt> blackListedJwtTokenList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (jwtAuthenticationToken == null) { //meaning that the BearerTokenAuthenticationFilter resolver couldn't get a token...
            throw new AuthenticationCredentialsNotFoundException("Authentication Token not found!!!");
        }
        Jwt jwtToken = jwtAuthenticationToken.getToken();
        if (blackListedJwtTokenList.contains(jwtToken)) {
            throw new AccessDeniedException("Authentication Token is invalid");
        }
        doFilter(request, response, filterChain);
    }
}