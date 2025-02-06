package com.disa.authservice.model.register;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class RegisterRequest {

    private String firstName;
    private String secondName;
    private String username;
    private String email;
    private String password;
}
