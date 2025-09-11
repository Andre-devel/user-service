package br.com.andredevel.user.service.domain.service.impl;

import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import br.com.andredevel.user.service.domain.service.UserService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        UserId userId = user.getId();
        
        if (userId != null) {
            userRepository.findById(userId).ifPresentOrElse(existingUser -> 
                            update(existingUser, user),
                    () -> insert(user));
        } else {
            insert(user);
        }
        
        return user;
    }
    
    private void insert(User user) {
        user = userRepository.saveAndFlush(user);
    }
    
    private void update(User existingUser, User newUser) {
        User persistenceUser = merge(existingUser, newUser);
        entityManager.detach(persistenceUser);
        newUser = userRepository.saveAndFlush(persistenceUser);
    }
    
    private User merge(User existingUser, User newUser) {
        existingUser.setName(newUser.getName());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setPassword(newUser.getPassword());
        return existingUser;
    }

    @Override
    public void deleteById(UserId id) {
        userRepository.deleteById(id);
    }
}
