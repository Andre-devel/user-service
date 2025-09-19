package br.com.andredevel.user.service.domain.model.validator;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class FieldValidator {
    private FieldValidator() {}
    
    public static void requireValidEmail(String email) {
       requireValidEmail(email, "Invalid email");
    }
    
    public static void requireValidPassword(String password) {
        requireValidPassword(password, "Password must be at least 6 characters long");
    }

    private static void requireValidPassword(String password, String invalidPassword) {
        requireNotBlank(password, invalidPassword);
        
        if (password.length() < 6) {
            throw new IllegalArgumentException(invalidPassword);
        }   
    }

    public static void requireValidEmail(String email, String errorMessage) {
        requireNotBlank(email, errorMessage);
        
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    public static void requireNotBlank(String value) {
        requireNotBlank(value, "");
    }

    public static void requireNotBlank(String value, String errorMessage) {
        Objects.requireNonNull(value, errorMessage);

        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
}
