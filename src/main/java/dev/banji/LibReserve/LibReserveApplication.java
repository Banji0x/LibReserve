package dev.banji.LibReserve;

import dev.banji.LibReserve.config.configurationproperties.University;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableConfigurationProperties(value = University.class)
public class LibReserveApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibReserveApplication.class, args);
    }

}
