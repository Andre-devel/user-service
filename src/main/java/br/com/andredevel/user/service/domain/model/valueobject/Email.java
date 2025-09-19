package br.com.andredevel.user.service.domain.model.valueobject;

import br.com.andredevel.user.service.domain.model.validator.FieldValidator;
import jakarta.persistence.Embeddable;

@Embeddable
public record Email(String value) {
    // trim and toLowerCase
    
    public Email {
        FieldValidator.requireValidEmail(value);
        value = value.toLowerCase().trim();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
