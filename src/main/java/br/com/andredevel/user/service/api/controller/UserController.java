package br.com.andredevel.user.service.api.controller;

import br.com.andredevel.user.service.api.model.LoginInput;
import br.com.andredevel.user.service.api.model.UserInput;
import br.com.andredevel.user.service.api.model.UserOutput;
import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.model.valueobject.Password;
import br.com.andredevel.user.service.domain.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.findById(new UserId(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserInput userInput) {
        User newUser = User.builder()
                .id(new UserId())
                .name(userInput.name())
                .email(new Email(userInput.email()))
                .password(userInput.password())
                .build();
        
        return userService.save(newUser);
    }
    
    @PostMapping
    @RequestMapping ("/login")  
    public ResponseEntity<UserOutput> login(@RequestBody LoginInput loginInput) {
        return ResponseEntity.ok(userService.login(loginInput));
    }   

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UserInput userInput) {
        User updatedUser = User.builder()
                .id(new UserId(id))
                .name(userInput.name())
                .email(new Email(userInput.email()))
                .password(userInput.password())
                .build();
        return ResponseEntity.ok(userService.save(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        return userService.findById(new UserId(id))
                .map(user -> {
                    userService.deleteById(new UserId(id));
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
