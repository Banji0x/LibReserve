package dev.banji.LibReserve.service;

import dev.banji.LibReserve.config.userDetails.StudentSecurityDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtEncoder encoder;
    private final List<String> jwtTokenBlackList;
    @Value("${jwt.expirationTime}")
    private Long expirationTime;

    public String generateJwt(Authentication authentication) {
        return generateJwt(authentication, this.expirationTime);
    }

    public String generateJwt(Authentication authenticatedToken, Long expirationTime) {
        var scope = authenticatedToken
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toString();
        JwtClaimsSet claims = JwtClaimsSet
                .builder()
                .issuedAt(Instant.now())
                .issuer("LibReserve")
                .expiresAt(Instant.now().plus(expirationTime, MINUTES))
                .subject(((StudentSecurityDetails) authenticatedToken.getPrincipal()).getMatricNumber())
                .claim("scope", scope.trim())
                .build();
        JwtEncoderParameters encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return encoder.encode(encoderParameters).getTokenValue();
    }

    public boolean blacklistJwt(String jwt) {
        return jwtTokenBlackList.add(jwt);
    }
}