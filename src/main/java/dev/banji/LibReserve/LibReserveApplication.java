package dev.banji.LibReserve;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableConfigurationProperties({LibraryConfigurationProperties.class})
@EnableMethodSecurity
@EnableScheduling
public class LibReserveApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibReserveApplication.class, args);
    }
}
