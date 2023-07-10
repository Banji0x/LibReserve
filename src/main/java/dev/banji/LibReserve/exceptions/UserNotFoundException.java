package dev.banji.LibReserve.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class UserNotFoundException extends AuthenticationException {
    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException LibrarianNotFoundException() {
        return new UserNotFoundException("Librarian not found.");
    }

    public static UserNotFoundException StudentNotFoundException() {
        return new UserNotFoundException("Student not found.");
    }
}
