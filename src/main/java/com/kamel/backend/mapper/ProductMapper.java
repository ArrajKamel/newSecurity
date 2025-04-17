package com.kamel.backend.mapper;

import com.kamel.backend.dto.ProductResponse;
import com.kamel.backend.dto.SellerDto;
import com.kamel.backend.model.Product;

public class ProductMapper {

    public static ProductResponse mapToDTO(Product product, SellerDto seller) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .model(product.getModel())
                .year(product.getYear())
                .used(product.isUsed())
                .fuelType(product.getFuelType())
                .transmission(product.getTransmission())
                .categoryName(product.getCategory().getName()) // will initialize here
                .seller(seller)
                .build();
    }
}
