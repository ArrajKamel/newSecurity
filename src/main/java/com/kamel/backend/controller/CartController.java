package com.kamel.backend.controller;

import com.kamel.backend.dto.AddToCartRequest;
import com.kamel.backend.model.Cart;
import com.kamel.backend.serivce.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {
    private final CartService _cartService;

    @Autowired
    public CartController(CartService cartService) {
        _cartService = cartService;
    }


    @PostMapping("/init")
    public ResponseEntity<?> initCart(){
        Cart cart = _cartService.createCart();
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
        //exceptions are handled globally in the exception handler
    }

    @PostMapping("/item")
    public ResponseEntity<?> addItemToCart(@RequestBody AddToCartRequest request){
        return new ResponseEntity<>(_cartService.addItemToCart(request), HttpStatus.OK);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItemsFromCart(){
        return new ResponseEntity<>(_cartService.getAllItems(), HttpStatus.OK);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<?> deleteItem(@PathVariable UUID cartItemId){
        _cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>("item deleted successfully", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable UUID cartId){
        _cartService.deleteCart(cartId);
        return new ResponseEntity<>("cart deleted successfully", HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/increase-item/{cartItemId}")
    public ResponseEntity<?> increaseCartItemQuantity(@PathVariable UUID cartItemId){
        int newQuantity = _cartService.updateCartItemQuantity(cartItemId, true);
        return new ResponseEntity<>("cart item's quantity has increased successfully, the new amount is: " + newQuantity, HttpStatus.OK);
    }

    @PatchMapping("/decrease-item/{cartItemId}")
    public ResponseEntity<?> decreaseCartItemQuantity(@PathVariable UUID cartItemId){
        int newQuantity = _cartService.updateCartItemQuantity(cartItemId, false);
        return new ResponseEntity<>("cart item's quantity has decreased successfully, the new amount is: " + newQuantity, HttpStatus.OK);
    }
}
