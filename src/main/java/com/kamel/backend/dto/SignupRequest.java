package com.kamel.backend.dto;

import com.kamel.backend.model.Role;
import lombok.Data;

@Data
public class SignupRequest {

    private String email;
    private String password;
    private String firstname;
    private boolean emailScanConsent;
    private String role; // you have to  inform the frontend dev about the valid values of roles (ROLE_BUYER, ROLE_SELLER, ROLE_BOSS)

}
