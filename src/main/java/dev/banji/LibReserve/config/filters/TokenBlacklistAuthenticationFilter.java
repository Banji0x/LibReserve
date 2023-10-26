package dev.banji.LibReserve.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TokenBlacklistAuthenticationFilter extends OncePerRequestFilter {
    private final List<Jwt> blackListedJwtTokenList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authenticatedJwt = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtToken = authenticatedJwt.getToken();
        if (blackListedJwtTokenList.contains(jwtToken)) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: Token is blacklisted");
            response.getWriter().flush();
            return;
        }
        doFilter(request, response, filterChain);
    }
}