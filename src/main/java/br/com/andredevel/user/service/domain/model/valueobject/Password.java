package br.com.andredevel.user.service.domain.model.valueobject;

import br.com.andredevel.user.service.domain.model.validator.FieldValidator;
import jakarta.persistence.Embeddable;

@Embeddable
public record Password(String value) {

    public Password {
        FieldValidator.requireValidPassword(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
