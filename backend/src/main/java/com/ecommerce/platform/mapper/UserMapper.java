package com.ecommerce.platform.mapper;

import com.ecommerce.platform.dto.response.UserResponse;
import com.ecommerce.platform.entity.Role;
import com.ecommerce.platform.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserResponse toResponse(User user);

    default Set<String> map(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

}