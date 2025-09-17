package br.com.andredevel.user.service.domain.service.impl;

import br.com.andredevel.user.service.api.model.LoginInput;
import br.com.andredevel.user.service.api.model.UserOutput;
import br.com.andredevel.user.service.domain.exception.AuthenticationFailedException;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.model.validator.BusinessRuleValidator;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import br.com.andredevel.user.service.domain.service.UserService;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final BusinessRuleValidator businessRuleValidator;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public UserServiceImpl(UserRepository userRepository, EntityManager entityManager, BusinessRuleValidator businessRuleValidator) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.businessRuleValidator = businessRuleValidator;
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
        businessRuleValidator.validateEmailUniqueness(user.getId(), user.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user = userRepository.saveAndFlush(user);
    }
    
    private void update(User existingUser, User newUser) {
        businessRuleValidator.validateEmailUniqueness(existingUser.getId(), newUser.getEmail());
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

    @Override
    public Optional<User> findByEmail(Email email) {
        return userRepository.findByEmail(email);   
    }

    @Override
    public UserOutput login(LoginInput loginInput) {
        Optional<User> userOpt = userRepository.findByEmail(new Email(loginInput.email()));
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (bCryptPasswordEncoder.matches(loginInput.password(), user.getPassword())) {
                return new UserOutput(user.getId().getValue(), user.getName(), user.getEmail().value());
            } else {
                throw new AuthenticationFailedException();
            }
        } else {
            throw new AuthenticationFailedException();
        }   
    }

    @Override
    public boolean isEmailUnique(Email email, UserId excludingUserId) {
        return userRepository.existsByEmailAndIdNot(email, excludingUserId);    
    }
}
