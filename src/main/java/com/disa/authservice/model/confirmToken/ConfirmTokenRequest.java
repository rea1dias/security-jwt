package com.disa.authservice.model.confirmToken;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ConfirmTokenRequest {

    private String token;
    private Long userId;
    private String createdTime;
    private String expiredTime;
    private String confirmedTime;
}
