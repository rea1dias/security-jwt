package com.disa.authservice.model.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String firstName;
    private String secondName;
    private String username;
    private String email;
    private String password;
}
