package com.kamel.backend.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderItemXmlDto {
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    // getters/setters or Lombok
}
