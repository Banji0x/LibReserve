package dev.banji.LibReserve.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class EmailDto {
    protected final @NotBlank.List({@NotBlank(message = "Email Address cannot be blank")}) @NotNull(message = "Email Address cannot be empty") List<String> to;
    protected final @NotBlank.List(@NotBlank(message = "Subject cannot be blank")) @NotNull.List(@NotNull(message = "Subject cannot be blank")) String subject;
    protected final @NotBlank.List(@NotBlank(message = "body cannot be blank")) @NotNull.List(@NotNull(message = "body cannot be blank")) String body;

    public EmailDto(String to, String subject, String body) {
        this.to = List.of(to);
        this.subject = subject;
        this.body = body;
    }

    public EmailDto(List<String> to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}