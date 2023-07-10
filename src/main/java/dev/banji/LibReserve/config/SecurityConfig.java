package dev.banji.LibReserve.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import dev.banji.LibReserve.config.filters.LibrarianAuthenticationFilter;
import dev.banji.LibReserve.config.filters.StudentAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final LibrarianAuthenticationFilter librarianAuthenticationFilter;
    private final StudentAuthenticationFilter studentAuthenticationFilter;
    @Value("${jwt.key}")
    private String jwtKey;

    public SecurityConfig(LibrarianAuthenticationFilter librarianAuthenticationFilter, StudentAuthenticationFilter studentAuthenticationFilter) {
        this.librarianAuthenticationFilter = librarianAuthenticationFilter;
        this.studentAuthenticationFilter = studentAuthenticationFilter;
    }

    //Security Filter Chain Configuration
    @Bean
    @Order(1)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/lib-reserve/token/student", "/lib-reserve/token/librarian")
                .addFilterBefore(studentAuthenticationFilter, AuthorizationFilter.class)
                .addFilterBefore(librarianAuthenticationFilter, StudentAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/lib-reserve/token/student").hasRole("STUDENT"); //authorization...
                    auth.requestMatchers("/lib-reserve/token/librarian").hasRole("LIBRARIAN"); //authorization...
                    auth.anyRequest().denyAll();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain studentSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/lib-reserve/student/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .addFilterBefore(studentAuthenticationFilter, AuthorizationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS))
                .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain librarianSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/lib-reserve/librarian/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .addFilterAfter(librarianAuthenticationFilter, StudentAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable) //disable csrf...
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS))
                .build();
    }

    @Bean
    @Order(4)
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