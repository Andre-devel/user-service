package br.com.andredevel.user.service.domain.exception;

import java.util.Map;

public class DomainException extends RuntimeException{

    private Map<String, Object> details;
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(String message) { 
        super(message);
    }
    
    public DomainException(String message, Map<String, Object> details) { 
        super(message);
        this.details = details;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
}
