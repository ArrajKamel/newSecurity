package com.kamel.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
}
