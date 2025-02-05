package com.mobile.crypto.dto;

import lombok.Data;

@Data
public class PinRequest {
    private String email;
    private String pin;
}
