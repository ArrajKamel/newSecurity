package com.kamel.backend.mapper;

import com.kamel.backend.dto.OrderXmlDto;
import com.kamel.backend.model.Order;
import com.kamel.backend.dto.OrderItemXmlDto;

import java.util.List;

public class OrderToXmlMapper {
    public static OrderXmlDto mapToXmlDto(Order order) {
        List<OrderItemXmlDto> itemDtos = order.getOrderItems().stream().map(item -> {
            OrderItemXmlDto dto = new OrderItemXmlDto();
            dto.setProductName(item.getProduct().getProductName());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getUnitPrice());
            return dto;
        }).toList();

        OrderXmlDto dto = new OrderXmlDto();
        dto.setOrderId(order.getOrderId());
        dto.setBuyerName(order.getBuyer().getUsername()); // or getFullName()
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus().name());
        dto.setItems(itemDtos);

        return dto;
    }

}
