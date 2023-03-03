package dev.banji.LibReserve.config.security;

import dev.banji.LibReserve.config.filters.authentication.LibrarianAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${university.config.librarian-login-url}")
    private String adminLoginPage;
    @Value("${university.config.student-login-url}")
    private String studentLoginPage;
    private final LibrarianAuthenticationFilter librarianAuthenticationFilter;

    public SecurityConfig(LibrarianAuthenticationFilter librarianAuthenticationFilter) {
        this.librarianAuthenticationFilter = librarianAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .addFilterBefore(librarianAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->
                        request.anyRequest().authenticated())
                .formLogin()
                .usernameParameter("matric-number")
                .passwordParameter("password")
                .loginPage("/lib-reserve/student/login")
                .successForwardUrl("/lib-reserve/welcome")
                .failureForwardUrl("/lib-reserve/student/login?error")
                .and()
                .build();
    }
}