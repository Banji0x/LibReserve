package dev.banji.LibReserve.config.properties;

import dev.banji.LibReserve.model.AllowedFaculties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "library.properties")
@Component
@Getter
@Setter
public final class LibraryConfigurationProperties {

    private Boolean acceptingBookings;
    private Long bookingTimeAllowedInMinutes;
    private Long numberOfSeats;
    private Boolean allowTimeExtension;
    private Long maximumTimeExtensionAllowedInMinutes;
    private Boolean allowLateCheckIn;
    private Long allowedTimeTillTokenExpirationInMinutes;
    private Boolean allowEarlyCheckIn;
    private Long allowedEarlyCheckInMinutes;
    private Boolean sendNotifications;
    private List<Integer> notificationTimeListInMinutes;
    private Long readTimeoutInSeconds;
    private Long connectTimeoutInSeconds;
    private Set<AllowedFaculties> setOfAllowedFaculties;
    private Boolean enableSeatRandomization;
    private Boolean allowMultipleBookings;
    private Boolean allowAdvancedBookings;
    private Integer maximumLimitPerDay;
    private Boolean enableLimitPerDay;
    private Boolean reserveLibrarianSeat;
    private Long numberOfLibrarians;
    private Boolean allowMultipleTimeExtension;

    public LibraryConfigurationProperties() {
        if (reserveLibrarianSeat) {
            numberOfSeats = numberOfSeats - numberOfLibrarians; //reserve spots for the librarian's.
        }
    }
}