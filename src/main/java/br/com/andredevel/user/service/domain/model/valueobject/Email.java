package br.com.andredevel.user.service.domain.model.valueobject;

import br.com.andredevel.user.service.domain.model.validator.FieldValidations;
import jakarta.persistence.Embeddable;

@Embeddable
public record Email(String value) {

    public Email {
        FieldValidations.requireValidEmail(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
