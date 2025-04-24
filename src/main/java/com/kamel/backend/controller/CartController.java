package com.kamel.backend.controller;

import com.kamel.backend.dto.AddToCartRequest;
import com.kamel.backend.model.Cart;
import com.kamel.backend.serivce.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/cart")
public class CartController {
    private final CartService _cartService;

    @Autowired
    public CartController(CartService cartService) {
        _cartService = cartService;
    }


    @PostMapping("/init")
    public ResponseEntity<?> initCart(@RequestParam UUID buyerId){
//        try {
            Cart cart = _cartService.createCart(buyerId);
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
//        }catch (EntityNotFoundException ex){
//            return  new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//        }catch (AccessDeniedException ex){
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
//        }
    }

    @PostMapping("/item")
    public ResponseEntity<?> addItemToCart(@RequestBody AddToCartRequest request){
        return new ResponseEntity<>(_cartService.addItemToCart(request), HttpStatus.OK);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItemsFromCart(@RequestParam UUID buyerId){
        return new ResponseEntity<>(_cartService.getAllItems(buyerId), HttpStatus.OK);
    }


}
