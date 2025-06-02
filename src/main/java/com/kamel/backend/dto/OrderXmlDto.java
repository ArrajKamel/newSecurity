package com.kamel.backend.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderXmlDto {
    private UUID orderId;
    private String buyerName;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private String status;

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<OrderItemXmlDto> items;

    // getters/setters or Lombok
}
