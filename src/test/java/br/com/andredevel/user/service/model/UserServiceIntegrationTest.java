package br.com.andredevel.user.service.model;

import br.com.andredevel.user.service.config.BaseIntegrationTest;
import br.com.andredevel.user.service.domain.exception.EmailInUseException;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import br.com.andredevel.user.service.domain.service.UserService;
import br.com.andredevel.user.service.util.UserTestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveNewUser() {
        User user = UserTestBuilder.existingUser().email(new Email("john@example.com")).build();

        User savedUser = userService.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail().value()).isEqualTo("john@example.com");
        assertThat(savedUser.getPassword()).isNotEqualTo("password123"); // Should be encoded
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        User existingUser = UserTestBuilder.existingUser().build();
        userService.save(existingUser);

        User duplicateUser = UserTestBuilder.existingUser()
                .name("Jane Doe")
                .build();

        assertThatThrownBy(() -> userService.save(duplicateUser))
                .isInstanceOf(EmailInUseException.class);
        }

    @Test
    void shouldFindAllUsers() {
        User user1 = UserTestBuilder.existingUser().build();
        User user2 = UserTestBuilder.existingUser()
                .email(new Email("jane@example.com"))
                .name("Jane Doe")
                .build();

        userService.save(user1);
        userService.save(user2);

        List<User> users = userService.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void shouldFindUserById() {
        User user = UserTestBuilder.existingUser().build();
        User savedUser = userService.save(user);

        Optional<User> foundUser = userService.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldFindUserByEmail() {
        User user = UserTestBuilder.existingUser().email(new Email("john@example.com")).build();
        userService.save(user);

        Optional<User> foundUser = userService.findByEmail(new Email("john@example.com"));

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldDeleteUserById() {
        User user = UserTestBuilder.existingUser().build();
        User savedUser = userService.save(user);

        userService.deleteById(savedUser.getId());

        Optional<User> foundUser = userService.findById(savedUser.getId());
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void shouldUpdateExistingUser() {
        User user = UserTestBuilder.existingUser().build();
        User savedUser = userService.save(user);

        User updatedUser = UserTestBuilder.existingUser()
                .id(savedUser.getId())
                .name("John Updated")
                .password("newpassword")
                .build();
        userService.save(updatedUser);

        Optional<User> foundUser = userService.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Updated");
    }

    @Test
    void shouldAllowSameEmailForSameUser() {
        User user = UserTestBuilder.existingUser().build();
        User savedUser = userService.save(user);

        User updatedUser = UserTestBuilder.existingUser()
                .id(savedUser.getId())
                .name("John Updated")
                .password("newpassword")
                .build();

        // Should not throw exception when updating same user with same email
        assertThatCode(() -> userService.save(updatedUser)).doesNotThrowAnyException();
    }
}