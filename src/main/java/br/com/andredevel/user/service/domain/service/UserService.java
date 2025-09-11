package br.com.andredevel.user.service.domain.service;

import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(UserId id);

    User save(User user);

    void deleteById(UserId id);
}
