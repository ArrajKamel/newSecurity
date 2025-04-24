package com.kamel.backend.mapper;

import com.kamel.backend.dto.*;
import com.kamel.backend.model.Cart;
import com.kamel.backend.model.CartItem;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.model.Product;

public class CartMapper {

    public static CartResponse mapToDTO(Cart cart, BuyerDto buyer) {
        return CartResponse.builder()
                .id(cart.getId())
                .buyer(buyer)
                .lastUpdated(cart.getLastUpdated())
                .build();
    }

    public static CartItemProductDto mapToDTO(Product product, SellerDto seller) {
        return CartItemProductDto.builder()
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

    public static CartItemDto mapToDTO(CartItem cartItem, CartItemProductDto product) {
        return CartItemDto.builder()
                .id(cartItem.getId())
                .product(product)
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .build();
    }


}
