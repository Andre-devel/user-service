package br.com.andredevel.user.service.domain.exception;

import static br.com.andredevel.user.service.domain.exception.ErrorMessage.VALIDATION_ERROR_EMAIL_IN_USE;

import java.util.Map;

public class EmailInUseException extends DomainException{
    public EmailInUseException(String email) {
        super(VALIDATION_ERROR_EMAIL_IN_USE, Map.of("email", email));
    }
}
