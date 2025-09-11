package br.com.andredevel.user.service.domain.model.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Embeddable
@EqualsAndHashCode
public class UserId implements Serializable {
    private UUID value;

    public UserId() {
        this(UUID.randomUUID());
    }

    public UserId(UUID value) {
        this.value = value;
    }

    public UserId(String value) {
        this.value = UUID.fromString(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
