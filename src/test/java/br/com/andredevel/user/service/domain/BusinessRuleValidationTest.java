package br.com.andredevel.user.service.domain;

import br.com.andredevel.user.service.domain.exception.DomainException;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.validator.EmailRuleValidator;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import br.com.andredevel.user.service.config.BaseIntegrationTest;
import br.com.andredevel.user.service.util.UserTestBuilder;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
public class BusinessRuleValidationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldValidateEmailUniqueness_WhenEmailDoesNotExist() {
        Email email = new Email("new@example.com");
        UserId userId = new UserId(UUID.randomUUID());
        
        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, userId, email))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyExistsForDifferentUser() {
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        User savedUser = userRepository.save(existingUser);
        
        UserId differentUserId = new UserId(UUID.randomUUID());
        Email existingEmail = new Email("existing@example.com");

        assertThatThrownBy(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, differentUserId, existingEmail))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldNotThrowException_WhenEmailExistsForSameUser() {
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        User savedUser = userRepository.save(existingUser);
        
        Email existingEmail = new Email("existing@example.com");

        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, savedUser.getId(), existingEmail))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldValidateEmailUniqueness_WhenUserIdIsNull() {
        Email email = new Email("new@example.com");

        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, null, email))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenUserIdIsNullAndEmailExists() {
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        userRepository.save(existingUser);
        
        Email existingEmail = new Email("existing@example.com");

        assertThatThrownBy(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, null, existingEmail))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldHandleMultipleUsers_AndValidateCorrectly() {
        User user1 = UserTestBuilder.existingUser()
                .email(new Email("user1@example.com"))
                .name("User One")
                .build();
        User user2 = UserTestBuilder.existingUser()
                .email(new Email("user2@example.com"))
                .name("User Two")
                .build();
        User user3 = UserTestBuilder.existingUser()
                .email(new Email("user3@example.com"))
                .name("User Three")
                .build();

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        
        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, savedUser1.getId(), new Email("user1@example.com")))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, savedUser2.getId(), new Email("user1@example.com")))
                .isInstanceOf(DomainException.class);

        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, savedUser3.getId(), new Email("newemail@example.com")))
                .doesNotThrowAnyException();
    }
}