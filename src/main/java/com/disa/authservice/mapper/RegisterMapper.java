package com.disa.authservice.mapper;

import com.disa.authservice.entity.User;
import com.disa.authservice.model.register.RegisterRequest;
import com.disa.authservice.model.register.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequest request);

    RegisterResponse toResponse(User user);



}
