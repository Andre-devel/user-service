package br.com.andredevel.user.service.domain;

import br.com.andredevel.user.service.config.BaseIntegrationTest;
import br.com.andredevel.user.service.domain.exception.EmailInUseException;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.validator.rule.EmailRuleValidator;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import br.com.andredevel.user.service.util.UserTestBuilder;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("new@example.com"))
                .build();
        
        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, existingUser))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyExistsForDifferentUser() {
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        User savedUser = userRepository.save(existingUser);
        
        User differentUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();

        assertThatThrownBy(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, differentUser))
                .isInstanceOf(EmailInUseException.class);
    }

    @Test
    void shouldNotThrowException_WhenEmailExistsForSameUser() {
        User existingUser = UserTestBuilder.existingUser()
                .email(new Email("existing@example.com"))
                .build();
        User savedUser = userRepository.save(existingUser);

        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, savedUser))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldHandleMultipleUsers_AndValidateCorrectly() {
        User user1 = UserTestBuilder.existingUser()
                .email(new Email("user1@example.com"))
                .name("User One")
                .build();
        User user2 = UserTestBuilder.existingUser()
                .email(new Email("user23213@example.com"))
                .name("User Two")
                .build();
        User user3 = UserTestBuilder.existingUser()
                .email(new Email("user3@example.com"))
                .name("User Three")
                .build();

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        
        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, savedUser1))
                .doesNotThrowAnyException();

        User userWithDuplicateEmail = UserTestBuilder.existingUser()
                .email(new Email("user1@example.com"))
                .name("User With Duplicate Email")
                .build();

        assertThatThrownBy(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, userWithDuplicateEmail))
                .isInstanceOf(EmailInUseException.class);

        User userWithNewEmail = UserTestBuilder.existingUser()
                .email(new Email("newemail@example.com"))
                .name("User With New Email")
                .build();

        assertThatCode(() -> EmailRuleValidator.validateEmailUniqueness(userRepository, userWithNewEmail))
                .doesNotThrowAnyException();
    }
}