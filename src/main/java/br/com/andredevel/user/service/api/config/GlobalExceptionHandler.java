package br.com.andredevel.user.service.api.config;

import br.com.andredevel.user.service.api.model.ErrorResponse;
import br.com.andredevel.user.service.domain.exception.AuthenticationFailedException;
import br.com.andredevel.user.service.domain.exception.EmailInUseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation error occurred: {}", errors);

        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Invalid input data",
            errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "BAD_REQUEST",
            ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(EmailInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailInUseException(EmailInUseException ex) {
        log.warn("Email in use: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "EMAIL_IN_USE",
            ex.getMessage(),
            ex.getDetails()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "AUTHENTICATION_FAILED",
            ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}