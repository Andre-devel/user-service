package br.com.andredevel.user.service.domain.model.validator;

import br.com.andredevel.user.service.domain.exception.DomainException;
import br.com.andredevel.user.service.domain.exception.EmailInUseException;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class BusinessRuleValidator {

    private final UserRepository userRepository;

    public BusinessRuleValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailUniqueness(UserId userId, Email email) {
        boolean existsByEmailAndIdNot = userRepository.existsByEmailAndIdNot(email,userId);
        if (existsByEmailAndIdNot) {
            throw new EmailInUseException(email.value());
        }   
    }
}