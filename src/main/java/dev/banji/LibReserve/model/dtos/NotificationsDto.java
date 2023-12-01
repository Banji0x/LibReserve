package dev.banji.LibReserve.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationsDto(
        @NotBlank(message = "user identifier cannot be blank") @NotNull(message = "user identifier cannot be blank") String userIdentifier,
        @NotBlank(message = "message cannot be blank") @NotNull(message = "message cannot be blank") String message) {
}
