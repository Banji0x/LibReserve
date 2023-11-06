package dev.banji.LibReserve.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import dev.banji.LibReserve.config.filters.JwtAccessTokenBlacklistAuthenticationFilter;
import dev.banji.LibReserve.config.filters.LibrarianAuthenticationFilter;
import dev.banji.LibReserve.config.filters.StudentAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LibrarianAuthenticationFilter librarianAuthenticationFilter;
    private final StudentAuthenticationFilter studentAuthenticationFilter;
    @Value("${jwt.key}")
    private String jwtKey;
    private final JwtAccessTokenBlacklistAuthenticationFilter jwtAccessTokenBlacklistAuthenticationFilter;

    private static void customize(SessionManagementConfigurer<HttpSecurity> sessionManagement) {
        sessionManagement.sessionCreationPolicy(STATELESS);
    }

    //Security Filter Chain Configuration
    @Bean
    public SecurityFilterChain JWTTokenGeneratorFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/api/lib-reserve/token/student", "/api/lib-reserve/token/librarian")
                .addFilterBefore(studentAuthenticationFilter, AuthorizationFilter.class)
                .addFilterBefore(librarianAuthenticationFilter, AuthorizationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/lib-reserve/token/student").hasRole("STUDENT"); //authorization...
                    auth.requestMatchers("/api/lib-reserve/token/librarian").hasRole("LIBRARIAN"); //authorization...
                    auth.anyRequest().denyAll();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(SecurityConfig::customize)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/api/lib-reserve/student/**", "/api/lib-reserve/librarian/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable) //disable csrf...
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .addFilterAfter(jwtAccessTokenBlacklistAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .sessionManagement(SecurityConfig::customize)
                .build();
    }

    @Bean
    public SecurityFilterChain h2ConsoleSecurityChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().disable())
                .build();
    }

    //Jwt Encoder and Decoders...
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey.getBytes()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtKey.getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, 0, keyBytes.length, "RSA");
        return NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(HS512).build();
    }

}