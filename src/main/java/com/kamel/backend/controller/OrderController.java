package com.kamel.backend.controller;

import com.kamel.backend.dto.OrderResponseDto;
import com.kamel.backend.dto.OrderXmlDto;
import com.kamel.backend.dto.placeOrderRequestDto;
import com.kamel.backend.model.Order;
import com.kamel.backend.serivce.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    private final OrderService _orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        _orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(  )  {
        // for the future, use the PlaceOrderRequestDto to ask the user for the address and the payment details
        OrderResponseDto orderResponseDto = _orderService.placeOrder();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderResponseDto);
    }

    @GetMapping(value = "/{orderId}/export", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<OrderXmlDto> exportOrderXml(@PathVariable UUID orderId) {
        OrderXmlDto xmlDto = _orderService.exportOrderAsXml(orderId);
        return ResponseEntity.ok(xmlDto);
    }

}
