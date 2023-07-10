package dev.banji.LibReserve.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.banji.LibReserve.config.tokens.LibrarianAuthenticationToken;
import dev.banji.LibReserve.config.tokens.StudentAuthenticationToken;
import dev.banji.LibReserve.config.userDetails.LibrarianSecurityDetails;
import dev.banji.LibReserve.config.userDetails.StudentSecurityDetails;
import dev.banji.LibReserve.exceptions.UserNotFoundException;
import dev.banji.LibReserve.model.Account;
import dev.banji.LibReserve.model.Librarian;
import dev.banji.LibReserve.model.Student;
import dev.banji.LibReserve.model.dtos.LibrarianLoginDetailsDto;
import dev.banji.LibReserve.model.dtos.StudentLoginDetailsDto;
import dev.banji.LibReserve.repository.LibrarianRepository;
import dev.banji.LibReserve.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ProjectConfig {
    //Request Matchers
    @Bean
    RequestMatcher librarianPathRequestMatcher() { //this request matcher is for token generation service
        return new AntPathRequestMatcher("/lib-reserve/token/librarian", "POST");
    }

    @Bean
    RequestMatcher studentPathRequestMatcher() { //this request matcher is for the token generation service
        return new AntPathRequestMatcher("/lib-reserve/token/student", "POST");
    }

    //UserDetailsService
    @Bean
    public UserDetailsService librarianUserDetailsService(LibrarianRepository librarianRepository) {
        return (staffNumber) -> {
            var user = librarianRepository.findByStaffNumber(staffNumber.trim().toLowerCase());
            Librarian librarian = user.orElseThrow(() -> new BadCredentialsException("Bad Credentials"));
            return new LibrarianSecurityDetails(librarian);
        };
    }

    @Bean
    public UserDetailsService studentUserDetailsService(StudentRepository studentRepository) {
        return (matricNumber) -> {
            var user = studentRepository.findByMatricNumber(matricNumber.trim().toLowerCase()); //first check if student already exists in database, if he is not a new user...
            return user.map(StudentSecurityDetails::new).orElse(null);
        };
    }

    //Authentication converters
    @Bean
    AuthenticationConverter librarianAuthConverter() {
        return (httpServletRequest) -> {
            String staffNumber = "";
            String password = "";
            try {
                var reduce = httpServletRequest.getReader().readLine();
                LibrarianLoginDetailsDto value = new ObjectMapper().readValue(reduce, LibrarianLoginDetailsDto.class);
                staffNumber = value.staffNumber();
                password = value.password();
            } catch (RuntimeException | IOException ignored) {
            }
            if ((!staffNumber.isBlank() && !password.isBlank()))
                return LibrarianAuthenticationToken.unauthenticated(staffNumber.trim(), password.trim()); //trimmed to remove whitespaces
            return LibrarianAuthenticationToken.unauthenticated(null, null);
        };
    }

    @Bean
    AuthenticationConverter studentAuthConverter() {
        return (httpServletRequest) -> {
            String matricNumber = "";
            String password = "";
            try {
                var reduce = httpServletRequest.getReader().readLine();
                StudentLoginDetailsDto value = new ObjectMapper().readValue(reduce, StudentLoginDetailsDto.class);
                matricNumber = value.matricNumber();
                password = value.password();
            } catch (RuntimeException | IOException ignored) {
            }
            if ((!matricNumber.isBlank() && !password.isBlank()))
                return StudentAuthenticationToken.unAuthenticatedToken(matricNumber.trim(), password.trim()); //trimmed to remove whitespaces
            return StudentAuthenticationToken.unAuthenticatedToken(null, null);
        };
    }

    //Authentication Success and Failure Handlers
    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return (httpServletRequest, httpServletResponse, authenticationException) -> {
            httpServletResponse.setContentType("application/json");
            String errorJson = new ObjectMapper().writeValueAsString(authenticationException.getMessage());
            httpServletResponse.getWriter().write(errorJson);
            if (authenticationException instanceof BadCredentialsException || authenticationException instanceof UserNotFoundException)
                httpServletResponse.setStatus(401);
            else if (authenticationException instanceof DisabledException || authenticationException instanceof LockedException)
                httpServletResponse.setStatus(423);
            else
                httpServletResponse.setStatus(400);
        };
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() { //this still needs work
        return (request, response, authentication) -> request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
    }

    //Rest Template
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean //this bean is required for the application.properties to function properly...
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        var placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//        Resource[] resources = new ClassPathResource[]{new ClassPathResource(PROPERTY_FILENAME)};
//        placeholderConfigurer.setLocations(resources);
//        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
//        return placeholderConfigurer;
//    }
}

