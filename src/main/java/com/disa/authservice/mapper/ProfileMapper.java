package com.disa.authservice.mapper;

import com.disa.authservice.entity.Profile;
import com.disa.authservice.model.profile.ProfileRequest;
import com.disa.authservice.model.profile.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Profile toEntity(ProfileRequest request);

    ProfileResponse toResponse(Profile profile);

}
