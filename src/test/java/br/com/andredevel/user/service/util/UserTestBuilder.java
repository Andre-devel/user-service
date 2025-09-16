package br.com.andredevel.user.service.util;

import br.com.andredevel.user.service.domain.model.entity.User;
import br.com.andredevel.user.service.domain.model.entity.UserId;
import br.com.andredevel.user.service.domain.model.valueobject.Email;
import br.com.andredevel.user.service.domain.model.entity.User.UserBuilder;
import br.com.andredevel.user.service.domain.model.valueobject.Password;


import java.util.UUID;

public class UserTestBuilder {

    private UserTestBuilder() {}
    
    public static UserBuilder existingUser() {
        return User.builder()
                .id(new UserId(UUID.randomUUID()))
                .name("John Doe")
                .email(new Email("email@hotmail.com"))
                .password(("123456"));  
        
    }
}