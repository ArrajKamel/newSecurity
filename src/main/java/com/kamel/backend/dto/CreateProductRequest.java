package com.kamel.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateProductRequest {

    @NotBlank
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull @Min(1)
    private int quantityAvailable;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull @Min(1900)
    private int year;

    private int engineCapacityCC;

    @NotBlank
    private String fuelType;

    @NotBlank
    private String transmission;

    private int mileageKm;

    @NotNull
    private boolean used;

//    public boolean getUsed(){ return used;}
}
