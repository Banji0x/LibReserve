package dev.banji.LibReserve.config;


import dev.banji.LibReserve.config.filters.tokens.LibrarianAuthenticationToken;
import dev.banji.LibReserve.config.security.SecurityLibrarian;
import dev.banji.LibReserve.exceptions.UserNotFoundException;
import dev.banji.LibReserve.model.Librarian;
import dev.banji.LibReserve.repository.LibrarianRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProjectConfig {
    @Value("${university.config.email-parameter}")
    private String emailParameter;
    @Value("${university.config.password-parameter}")
    private String passwordParameter;
    @Value("${university.config.librarian-login-url}")
    private String librarianLoginUrl;

    @Bean
    RequestMatcher librarianLoginRequestMatcher() {
        return new 
    }

    @Bean
    public UserDetailsService adminUserDetailsService(LibrarianRepository librarianRepository) {
        return (emailAddress) -> {
            var user = librarianRepository.findByEmailAddress(emailAddress);
            Librarian librarian = user.orElseThrow(UserNotFoundException::LibrarianNotFoundException);
            return new SecurityLibrarian(librarian);
        };
    }

    @Bean
    AuthenticationConverter librarianAuthConverter() {
        return (httpServletRequest) -> {
            System.out.println("This is the httpServlet that came in... " + httpServletRequest.getServletPath() + httpServletRequest.getMethod());
            String emailAddress = httpServletRequest.getParameter(emailParameter);
            String password = httpServletRequest.getParameter(passwordParameter);
            if (emailAddress == null || password == null)
                return null;
            return LibrarianAuthenticationToken.unauthenticated(emailAddress.trim(), password);
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

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

