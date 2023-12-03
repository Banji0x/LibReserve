package dev.banji.LibReserve.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public abstract class WebNotificationDto {
    @NotBlank.List(@NotBlank(message = "user identifier cannot be blank"))
    @NotNull.List(@NotNull(message = "user identifier cannot be blank"))
    protected final List<String> userIdentifier;
    @NotNull(message = "Time left cannot be null")
    protected final Long timeLeft;
    @NotBlank(message = "message cannot be blank")
    @NotNull(message = "message cannot be blank")
    protected final String message;

    public WebNotificationDto(String userIdentifier, String message, Long timeLeft) {
        this.userIdentifier = List.of(userIdentifier);
        this.timeLeft = timeLeft;
        this.message = message;
    }

    public WebNotificationDto(List<String> userIdentifier, Long timeLeft, String message) {
        this.userIdentifier = userIdentifier;
        this.timeLeft = timeLeft;
        this.message = message;
    }

    public List<String> userIdentifier() {
        return userIdentifier;
    }


    public Long timeLeft() {
        return timeLeft;
    }

    public String message() {
        return message;
    }

    public static class SingleWebNotificationDto extends WebNotificationDto {

        public SingleWebNotificationDto(String userIdentifier, String message, Long timeLeft) {
            super(userIdentifier, message, timeLeft);
        }
    }

    public static class BulkWebNotificationDto extends WebNotificationDto {
        public BulkWebNotificationDto(List<String> userIdentifier, Long timeLeft, String message) {
            super(userIdentifier, timeLeft, message);
        }
    }
}
