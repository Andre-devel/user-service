package br.com.andredevel.user.service.domain.repository;

import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {
    
    Optional<User> findByEmail(Email email);
    boolean existsByEmailAndIdNot(Email email, UserId id);
    
}
