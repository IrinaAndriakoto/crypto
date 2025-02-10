package com.mobile.crypto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 9 , message = "Le numero de telephone doit avoir au moins 9 caracteres")
    private String phoneNumber;
}
