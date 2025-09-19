package br.com.andredevel.user.service.domain.model.validator.rule;

import br.com.andredevel.user.service.domain.exception.EmailInUseException;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.repository.UserRepository;

public final class EmailRuleValidator {

    private EmailRuleValidator() {
    }

    public static void  validateEmailUniqueness(UserRepository userRepository, User user) {
        boolean existsByEmailAndIdNot = userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId());
        if (existsByEmailAndIdNot) {
            throw new EmailInUseException(user.getEmail().value());
        }
    }
}