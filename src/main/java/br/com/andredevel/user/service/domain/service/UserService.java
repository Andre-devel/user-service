package br.com.andredevel.user.service.domain.service;

import aj.org.objectweb.asm.commons.Remapper;
import br.com.andredevel.user.service.api.model.LoginInput;
import br.com.andredevel.user.service.api.model.UserOutput;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(UserId id);

    User save(User user);

    void deleteById(UserId id);

    Optional<User> findByEmail(Email email);

    UserOutput login(LoginInput loginInput);
}
