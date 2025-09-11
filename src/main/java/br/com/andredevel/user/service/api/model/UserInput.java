package br.com.andredevel.user.service.api.model;

import br.com.andredevel.user.service.domain.model.valueobject.Email;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public record UserInput(String name, String email, String password) {
}
