package br.com.andredevel.user.service.controller;

import br.com.andredevel.user.service.api.model.LoginInput;
import br.com.andredevel.user.service.api.model.UserInput;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.repository.UserRepository;
import br.com.andredevel.user.service.domain.service.UserService;
import br.com.andredevel.user.service.config.BaseIntegrationTest;
import br.com.andredevel.user.service.util.UserTestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
public class UserControllerTest extends BaseIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserInput userInput = new UserInput("John Doe", "john@example.com", "password123");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnValidationErrorsForInvalidInput() throws Exception {
        UserInput invalidInput = new UserInput("", "invalid-email", "123");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Create user with encoded password
        User user = UserTestBuilder.existingUser()
                .password("password123")
                .email(new Email("john@example.com"))
                .build();
        userService.save(user);

        LoginInput loginInput = new LoginInput("john@example.com", "password123");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidLogin() throws Exception {
        // Create user
        User user = UserTestBuilder.existingUser().build();
        userService.save(user);

        LoginInput invalidLogin = new LoginInput("john@example.com", "wrongpassword");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenUserNotFound() throws Exception {
        LoginInput loginInput = new LoginInput("nonexistent@example.com", "password123");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInput)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        // Create test users
        User user1 = UserTestBuilder.existingUser().build();
        User user2 = UserTestBuilder.existingUser()
                .email(new Email("jane@example.com"))
                .name("Jane Doe")
                .build();

        userService.save(user1);
        userService.save(user2);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = UserTestBuilder.existingUser().name("John Doe").email(new Email("john@example.com")).build();
        User savedUser = userService.save(user);

        mockMvc.perform(get("/users/" + savedUser.getId().getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email.value", is("john@example.com")));
    }

    @Test
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/users/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        User user = UserTestBuilder.existingUser().build();
        User savedUser = userService.save(user);

        UserInput updateInput = new UserInput("John Updated", "john@example.com", "newpassword123");

        mockMvc.perform(put("/users/" + savedUser.getId().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Updated")));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithDuplicateEmail() throws Exception {
        // Create two users
        User user1 = UserTestBuilder.existingUser().build();
        User user2 = UserTestBuilder.existingUser()
                .email(new Email("john@example.com"))
                .name("Jane Doe")
                .build();

        User savedUser1 = userService.save(user1);
        User savedUser2 = userService.save(user2);

        // Try to update user2 with user1's email
        UserInput updateInput = new UserInput("Jane Updated", "john@example.com", "newpassword123");

        mockMvc.perform(put("/users/" + savedUser1.getId().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInput)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        User user = UserTestBuilder.existingUser().build();
        User savedUser = userService.save(user);

        mockMvc.perform(delete("/users/" + savedUser.getId().getValue()))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        mockMvc.perform(get("/users/" + savedUser.getId().getValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/users/" + nonExistentId))
                .andExpect(status().isNotFound());
    }
}