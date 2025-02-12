package com.disa.authservice.model.reset;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetTokenRequest {

    private String token;
    private String newPassword;
}
