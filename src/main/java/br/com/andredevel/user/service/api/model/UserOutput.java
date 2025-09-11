package br.com.andredevel.user.service.api.model;

import java.util.UUID;

public record UserOutput(UUID id, String name, String email) {
}       
