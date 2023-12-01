package dev.banji.LibReserve.config.properties;

import dev.banji.LibReserve.exceptions.LibraryRuntimeException;
import dev.banji.LibReserve.model.AllowedFaculties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "library.properties")
@Getter
@Setter
public final class LibraryConfigurationProperties {
    private String universityUrl;
    private Boolean acceptingBookings;
    private Long bookingTimeAllowedInMinutes;
    private Long recommendedCheckInTime; //this can be 3-5 minutes before the bookedTime.
    private Long numberOfSeats;
    private Boolean allowTimeExtension;
    private Long maximumTimeExtensionAllowedInMinutes;
    private Boolean allowLateCheckIn;
    private Long allowedLateCheckInTimeInMinutes;
    private Boolean allowEarlyCheckIn;
    private Long allowedEarlyCheckInMinutes; //this should be above the recommendedCheckIn time.
    private Boolean sendNotifications;
    private Boolean sendEmails;
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

    @ConstructorBinding
    public LibraryConfigurationProperties(String universityUrl, Boolean acceptingBookings, Long bookingTimeAllowedInMinutes, Long recommendedCheckInTime, Long numberOfSeats, Boolean allowTimeExtension, Long maximumTimeExtensionAllowedInMinutes, Boolean allowLateCheckIn, Long allowedLateCheckInTimeInMinutes, Boolean allowEarlyCheckIn, Long allowedEarlyCheckInMinutes, Boolean sendNotifications, List<Integer> notificationTimeListInMinutes, Long readTimeoutInSeconds, Long connectTimeoutInSeconds, Set<AllowedFaculties> setOfAllowedFaculties, Boolean enableSeatRandomization, Boolean allowMultipleBookings, Boolean allowAdvancedBookings, Integer maximumLimitPerDay, Boolean enableLimitPerDay, Boolean reserveLibrarianSeat, Long numberOfLibrarians, Boolean allowMultipleTimeExtension, Boolean sendEmails) {
        this.universityUrl = universityUrl;
        this.acceptingBookings = acceptingBookings;
        this.bookingTimeAllowedInMinutes = bookingTimeAllowedInMinutes;
        this.recommendedCheckInTime = recommendedCheckInTime;
        this.numberOfSeats = numberOfSeats;
        this.allowTimeExtension = allowTimeExtension;
        this.maximumTimeExtensionAllowedInMinutes = maximumTimeExtensionAllowedInMinutes;
        this.allowLateCheckIn = allowLateCheckIn;
        this.allowedLateCheckInTimeInMinutes = allowedLateCheckInTimeInMinutes;
        this.allowEarlyCheckIn = allowEarlyCheckIn;
        this.allowedEarlyCheckInMinutes = allowedEarlyCheckInMinutes;
        this.sendNotifications = sendNotifications;
        this.sendEmails = sendEmails;
        this.notificationTimeListInMinutes = notificationTimeListInMinutes;
        this.readTimeoutInSeconds = readTimeoutInSeconds;
        this.connectTimeoutInSeconds = connectTimeoutInSeconds;
        this.setOfAllowedFaculties = setOfAllowedFaculties;
        this.enableSeatRandomization = enableSeatRandomization;
        this.allowMultipleBookings = allowMultipleBookings;
        this.allowAdvancedBookings = allowAdvancedBookings;
        this.maximumLimitPerDay = maximumLimitPerDay;
        this.enableLimitPerDay = enableLimitPerDay;
        this.reserveLibrarianSeat = reserveLibrarianSeat;
        this.numberOfLibrarians = numberOfLibrarians;
        this.allowMultipleTimeExtension = allowMultipleTimeExtension;
    }

    public void setRecommendedCheckInTime(Long recommendedCheckInTime) {
        if ((allowEarlyCheckIn) && (recommendedCheckInTime > allowedEarlyCheckInMinutes) && (recommendedCheckInTime > allowedLateCheckInTimeInMinutes)) // the recommendedTime cannot be above the allowedCheckInTime and the lateCheckInTIme
            throw new LibraryRuntimeException();
        this.recommendedCheckInTime = recommendedCheckInTime;
    }

    public void setAllowedEarlyCheckInMinutes(Long allowedEarlyCheckInMinutes) {
        if (allowedEarlyCheckInMinutes < recommendedCheckInTime) throw new LibraryRuntimeException();
        if (!allowEarlyCheckIn) allowEarlyCheckIn = true;
        this.allowedEarlyCheckInMinutes = allowedEarlyCheckInMinutes;
    }

    public void setAllowedLateCheckInTimeInMinutes(Long allowedLateCheckInTimeInMinutes) {
        if (allowedLateCheckInTimeInMinutes < recommendedCheckInTime) throw new LibraryRuntimeException();
        if (!allowLateCheckIn) allowLateCheckIn = true;
        this.allowedLateCheckInTimeInMinutes = allowedLateCheckInTimeInMinutes;
    }
}