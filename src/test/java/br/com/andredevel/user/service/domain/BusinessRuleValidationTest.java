package br.com.andredevel.user.service.domain;

import br.com.andredevel.user.service.domain.exception.DomainException;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.validator.BusinessRuleValidator;
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
    private BusinessRuleValidator businessRuleValidator;

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

        // Should not throw any exception
        assertThatCode(() -> businessRuleValidator.validateEmailUniqueness(userId, email))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyExistsForDifferentUser() {
        // Create and save a user
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        User savedUser = userRepository.save(existingUser);

        // Try to validate the same email for a different user
        UserId differentUserId = new UserId(UUID.randomUUID());
        Email existingEmail = new Email("existing@example.com");

        assertThatThrownBy(() -> businessRuleValidator.validateEmailUniqueness(differentUserId, existingEmail))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldNotThrowException_WhenEmailExistsForSameUser() {
        // Create and save a user
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        User savedUser = userRepository.save(existingUser);

        // Validate the same email for the same user (update scenario)
        Email existingEmail = new Email("existing@example.com");

        assertThatCode(() -> businessRuleValidator.validateEmailUniqueness(savedUser.getId(), existingEmail))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldValidateEmailUniqueness_WhenUserIdIsNull() {
        // For new user creation scenario
        Email email = new Email("new@example.com");

        assertThatCode(() -> businessRuleValidator.validateEmailUniqueness(null, email))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenUserIdIsNullAndEmailExists() {
        // Create and save a user
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        userRepository.save(existingUser);

        // Try to validate the same email with null userId (new user scenario)
        Email existingEmail = new Email("existing@example.com");

        assertThatThrownBy(() -> businessRuleValidator.validateEmailUniqueness(null, existingEmail))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldHandleMultipleUsers_AndValidateCorrectly() {
        // Create multiple users
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

        // Should allow same user to keep their email
        assertThatCode(() -> businessRuleValidator.validateEmailUniqueness(savedUser1.getId(), new Email("user1@example.com")))
                .doesNotThrowAnyException();

        // Should prevent different user from using existing email
        assertThatThrownBy(() -> businessRuleValidator.validateEmailUniqueness(savedUser2.getId(), new Email("user1@example.com")))
                .isInstanceOf(DomainException.class);
        

        // Should allow new email for existing user
        assertThatCode(() -> businessRuleValidator.validateEmailUniqueness(savedUser3.getId(), new Email("newemail@example.com")))
                .doesNotThrowAnyException();
    }
}