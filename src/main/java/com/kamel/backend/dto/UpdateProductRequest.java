package com.kamel.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer quantityAvailable;
    private String brand;
    private String model;
    private Integer year;
    private Integer engineCapacityCC;
    private String fuelType;
    private String transmission;
    private Integer mileageKm;
    private String categoryName; // optional if category can be changed
    private Boolean used;
}
