package com.kamel.backend.mapper;

import com.kamel.backend.dto.OrderItemDto;
import com.kamel.backend.dto.OrderResponseDto;
import com.kamel.backend.dto.ProductResponse;
import com.kamel.backend.dto.SellerDto;
import com.kamel.backend.model.Order;
import com.kamel.backend.model.Product;

import java.time.LocalDateTime;

public class OrderMapper {

    public static OrderResponseDto mapToDTO(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .placedAt(LocalDateTime.now())
                .status(order.getStatus().name())
                .items(order.getOrderItems().stream()
                        .map(orderItem ->
                                OrderItemDto.builder()
                                        .unitPrice(orderItem.getUnitPrice())
                                        .quantity(orderItem.getQuantity())
                                        .productName(orderItem.getProduct().getProductName())
                                        .build())
                        .toList())
                .build();
    }
}
