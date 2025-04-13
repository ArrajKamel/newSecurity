package com.kamel.backend.dto;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;
    private String password;
    private boolean emailScanConsent;

}
