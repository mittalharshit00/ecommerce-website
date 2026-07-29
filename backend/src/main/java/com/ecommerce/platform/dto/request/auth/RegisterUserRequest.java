package com.ecommerce.platform.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

}