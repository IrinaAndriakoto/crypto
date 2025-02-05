package com.mobile.crypto.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignupRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Le mot de passe doit avoir au moins 6 caractères")
    private String password;
}
