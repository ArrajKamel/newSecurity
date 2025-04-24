package com.kamel.backend.dto;

import com.kamel.backend.model.Product;
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
public class CartItemDto {

    private UUID id;

    private CartItemProductDto product;

    private int quantity;

    private BigDecimal unitPrice;
}
