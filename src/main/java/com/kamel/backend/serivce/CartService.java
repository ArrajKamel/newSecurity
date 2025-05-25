package com.kamel.backend.serivce;

import com.kamel.backend.dto.*;
import com.kamel.backend.exception.CartExistsException;
import com.kamel.backend.exception.StockUnavailableException;
import com.kamel.backend.mapper.BuyerMapper;
import com.kamel.backend.mapper.CartMapper;
import com.kamel.backend.mapper.SellerMapper;
import com.kamel.backend.model.Cart;
import com.kamel.backend.model.CartItem;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.model.Product;
import com.kamel.backend.repo.CartItemRepo;
import com.kamel.backend.repo.CartRepo;
import com.kamel.backend.security.CostumeUserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {

    private final CartItemRepo _cartItemRepo;
    private final CartRepo _cartRepo;
    private final UserService _userService;
    private final ProductService _productService;

    @Autowired
    public CartService(CartItemRepo cartItemRepo, CartRepo cartRepo, UserService userService, ProductService productService) {
        _cartItemRepo = cartItemRepo;
        _cartRepo = cartRepo;
        _userService = userService;
        _productService = productService;
    }

    public Cart createCart() {

        MyUser buyer = _userService.getUserById(_userService.getCurrentUserId());
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }

        if(!buyer.hasRole("BUYER")){
            throw new AccessDeniedException("Only BUYERs can own carts");
        }

        if(_cartRepo.existsCartByBuyer_Id(_userService.getCurrentUserId())) {
            throw new CartExistsException();
        }

        return _cartRepo.save(
                Cart.builder()
                        .buyer(buyer)
                        .build()
        );

    }

    @Transactional
    public synchronized CartResponse addItemToCart(AddToCartRequest request) {
        MyUser buyer = _userService.getUserById(_userService.getCurrentUserId());
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }

        Product product = _productService.getFullProductById(request.getProductId());// this method throw and exception EntityNotFoundException
        if (product == null) {
            throw new EntityNotFoundException("Product not found");
        }

        Cart cart = _cartRepo.findCartByBuyer(buyer);
        if (cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }

        // Check if the product is already in the cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            System.out.println("am i enter this scope");
            // If the product is already in the cart, update the quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if(product.getQuantityAvailable() >= newQuantity) {
                existingItem.setQuantity(newQuantity);
                _cartItemRepo.save(existingItem); // Save the updated cart item
            }else {
                throw new StockUnavailableException("Insufficient stock for the requested quantity");
            }
        }else {
            // If the product is not in the cart, create a new cart item
            if (product.getQuantityAvailable() >= request.getQuantity()) {
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(request.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();
                _cartItemRepo.save(newItem);
            } else {
                throw new StockUnavailableException("Insufficient stock for the requested quantity");
            }
        }

        BuyerDto buyerDto = BuyerMapper.buyerInCart(buyer);
        return CartMapper.mapToDTO(cart, buyerDto);
    }

    public List<CartItemDto> getAllItems() {

        MyUser buyer = _userService.getUserById(_userService.getCurrentUserId());
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }
        Cart cart = _cartRepo.findCartByBuyer(buyer);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }

        // Use DTOs to avoid lazy loading issues

        if(cart.getItems() == null) {
            return List.of();
        }
        return cart.getItems().stream()
                .map(item -> {
                    SellerDto seller = SellerMapper.sellerInProduct(_userService.getUserById(item.getProduct().getSeller().getId()));
                    CartItemProductDto productDto = CartMapper.mapToDTO(item.getProduct(), seller);
                    return CartMapper.mapToDTO(item, productDto);

                })
                .toList();
    }

    public void deleteCartItem(UUID cartItemId) {
        MyUser buyer = _userService.getCurrentUser();
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }
        Cart cart = _cartRepo.findCartByBuyer(buyer);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }
        List<CartItem> items = cart.getItems();
        boolean ownsCartItem = items.stream()
                .anyMatch(item -> item.getId().equals(cartItemId));
        if(!ownsCartItem) {
            throw new EntityNotFoundException("This cart item is not yours");
        }

        CartItem item = items.stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        items.remove(item);
        _cartItemRepo.deleteById(cartItemId);
    }

    public void deleteCart(UUID cartId) {
        MyUser buyer = _userService.getCurrentUser();
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }
        Cart cart = _cartRepo.findCartByBuyer(buyer);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }
        if(!cart.getId().equals(cartId)) {
            throw new EntityNotFoundException("This cart is not yours");
        }
        _cartRepo.deleteById(cartId);
    }

    public int updateCartItemQuantity(UUID cartItemId, boolean operation) {
        MyUser buyer = _userService.getCurrentUser();
        if(buyer == null){
            throw new EntityNotFoundException("buyer not found");
        }
        Cart cart = _cartRepo.findCartByBuyer(buyer);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        // operation = true -> increment quantity
        if(operation) {
            if(item.getQuantity() >= item.getProduct().getQuantityAvailable()) {
                throw new StockUnavailableException("Insufficient stock for the requested quantity");
            }
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
        }else{
            if(item.getQuantity() <= 1) {
                throw new StockUnavailableException("you have only one item in your cart, delete it it instead of decrementing it");
            }
            int newQuantity = item.getQuantity() - 1;
            item.setQuantity(newQuantity);
        }
        return _cartItemRepo.save(item).getQuantity();
    }

    protected Cart findByBuyer(MyUser buyer){
        return _cartRepo.findCartByBuyer(buyer);
    }
}
