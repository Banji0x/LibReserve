package dev.banji.LibReserve.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EmailDto(
        @NotBlank(message = "Email Address cannot be blank") @NotNull(message = "Email Address cannot be blank") String to,
        @NotBlank(message = "Subject cannot be blank") @NotNull(message = "Subject cannot be blank") String subject,
        @NotBlank(message = "body cannot be blank") @NotNull(message = "body cannot be blank") String body) {
    public static record BulkEmailDto(List<String> to, String subject, String body) {
    }
}