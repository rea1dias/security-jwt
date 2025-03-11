package com.disa.authservice.model.profile;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ProfileRequest {

    private String fullName;
    private String bio;
    private String avatarUrl;
    private int followersCount;
    private int followingCount;
}
