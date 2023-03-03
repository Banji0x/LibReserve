package dev.banji.LibReserve.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
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
