package br.com.andredevel.user.service.domain.exception;

import static br.com.andredevel.user.service.domain.exception.ErrorMessage.AUTH_INVALID_EMAIL_OR_PASSWORD;

public class AuthenticationFailedException extends DomainException {
    
    public AuthenticationFailedException() {
        super(AUTH_INVALID_EMAIL_OR_PASSWORD);
    }   
}
