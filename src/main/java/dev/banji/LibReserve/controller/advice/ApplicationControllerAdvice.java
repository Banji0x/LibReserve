package dev.banji.LibReserve.controller.advice;

import dev.banji.LibReserve.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

}