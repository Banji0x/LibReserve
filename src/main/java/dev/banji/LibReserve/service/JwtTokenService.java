package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.tokens.LibrarianAuthenticationToken;
import dev.banji.LibReserve.config.tokens.StudentAuthenticationToken;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtEncoder encoder;
    private final List<Jwt> jwtTokenBlackList;
    @Value("${jwt.expirationTime}")
    private Long expirationTime;

    public String generateAccessToken(Authentication authenticatedToken) {
        return generateAccessToken(authenticatedToken, this.expirationTime);
    }

    private String generateAccessToken(Authentication authenticatedToken, @NonNull Long expirationTime) {
        var scope = authenticatedToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).toString();
        var userIdentifier = authenticatedToken.getPrincipal() instanceof LibrarianAuthenticationToken ?
                (((LibrarianAuthenticationToken) authenticatedToken.getPrincipal()).getStaffNumber()) :
                (((StudentAuthenticationToken) authenticatedToken.getPrincipal()).getMatricNumber());
        assert userIdentifier != null;
        JwtClaimsSet claims = JwtClaimsSet.builder().issuedAt(Instant.now()).issuer("LibReserve").expiresAt(Instant.now().plus(expirationTime, MINUTES)).subject(userIdentifier).claim("scope", scope.trim()).build();
        JwtEncoderParameters encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return encoder.encode(encoderParameters).getTokenValue();
    }

    public boolean blacklistAccessToken(Jwt jwt) {
        return jwtTokenBlackList.add(jwt);
    }
}