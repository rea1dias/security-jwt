package com.disa.authservice.service.impl;

import com.disa.authservice.entity.Profile;
import com.disa.authservice.mapper.ProfileMapper;
import com.disa.authservice.model.profile.ProfileRequest;
import com.disa.authservice.model.profile.ProfileResponse;
import com.disa.authservice.repo.ProfileRepository;
import com.disa.authservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        Profile profile = profileMapper.toEntity(request);
        return profileMapper.toResponse(profileRepository.save(profile));
    }
}
