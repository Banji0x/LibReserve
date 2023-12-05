package dev.banji.LibReserve.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.banji.LibReserve.exceptions.LibraryRuntimeException;
import dev.banji.LibReserve.exceptions.SeatNumberNotWithinRangeException;
import dev.banji.LibReserve.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationControllerAdvice {
    @ExceptionHandler
    ProblemDetail accessIsDeniedException(AccessDeniedException accessDeniedException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
    }

    @ExceptionHandler
    ProblemDetail badCredentialsException(BadCredentialsException badCredentialsException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, badCredentialsException.getMessage());
    }

    @ExceptionHandler
    ProblemDetail userNotFoundException(UserNotFoundException userNotFoundException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, userNotFoundException.getMessage());
    }

    @ExceptionHandler
    ProblemDetail seatNumberNotWithinRangeException(SeatNumberNotWithinRangeException seatNumberNotWithinRangeException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, seatNumberNotWithinRangeException.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ProblemDetail jsonProcessingException(JsonProcessingException jsonProcessingException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error: Invalid JSON data.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error: Unable to read JSON data.");
    }

    @ExceptionHandler(LibraryRuntimeException.class)
    ProblemDetail libraryRuntimeException(LibraryRuntimeException libraryRuntimeException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, libraryRuntimeException.getMessage());
    }
}