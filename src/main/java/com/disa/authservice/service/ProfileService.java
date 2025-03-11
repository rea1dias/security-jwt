package com.disa.authservice.service;

import com.disa.authservice.model.profile.ProfileRequest;
import com.disa.authservice.model.profile.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);
}
