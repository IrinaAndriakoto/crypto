package com.mobile.crypto.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}