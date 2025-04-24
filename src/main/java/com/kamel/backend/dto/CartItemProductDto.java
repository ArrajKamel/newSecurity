package com.kamel.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemProductDto {
    private UUID productId;
    private String productName;
    private String brand;
    private BigDecimal price;
    private String model;
    private int year;
    private boolean used;
    private String fuelType;
    private String transmission;
    private String categoryName;
    private SellerDto seller;
}
