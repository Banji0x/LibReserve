package dev.banji.LibReserve.config.configurationproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("university.config")
public record University(String studentLoginUrl, String librarianLoginUrl,
                         String internalUniversityUrl, String emailParameter,
                         String passwordParameter,
                         Boolean librarianLoginUrlPostOnly) {
}
