package dev.banji.LibReserve.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
public class TokenService {
    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateJwt(Authentication authenticatedToken) {
        Instant now = Instant.now();
        var scopeList = authenticatedToken
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        String[] splitStrings = StringUtils.split(scopeList.get(0), "_");
        assert splitStrings != null;
        assert splitStrings[1] != null;
        var scope = splitStrings[1];
        JwtClaimsSet claims = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(now.plus(1, HOURS))
                .subject(authenticatedToken.getName())
                .claim("scope", scope.trim())
                .build();
        JwtEncoderParameters encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return encoder.encode(encoderParameters).getTokenValue();
    }
}