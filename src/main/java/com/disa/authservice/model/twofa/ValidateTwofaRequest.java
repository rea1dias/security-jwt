package com.disa.authservice.model.twofa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTwofaRequest {

    private String email;
    private int code;
}
