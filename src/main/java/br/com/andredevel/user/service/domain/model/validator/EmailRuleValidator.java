package br.com.andredevel.user.service.domain.model.validator;

import br.com.andredevel.user.service.domain.exception.EmailInUseException;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.repository.UserRepository;

public final class EmailRuleValidator {

    private EmailRuleValidator() {
    }

    public static void validateEmailUniqueness(UserRepository userRepository, UserId userId, Email email) {
        boolean existsByEmailAndIdNot = userRepository.existsByEmailAndIdNot(email, userId);
        if (existsByEmailAndIdNot) {
            throw new EmailInUseException(email.value());
        }
    }
}