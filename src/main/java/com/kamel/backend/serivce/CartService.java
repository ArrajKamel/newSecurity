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

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CostumeUserPrincipal principal = (CostumeUserPrincipal) authentication.getPrincipal();
        return principal.getId();
    }

    public Cart createCart() {

        System.out.println("the one from security context holder : " + getCurrentUserId());
        MyUser buyer = _userService.getUserById(getCurrentUserId());
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }

        if(!buyer.hasRole("BUYER")){
            throw new AccessDeniedException("Only BUYERs can own carts");
        }

        if(_cartRepo.existsCartByBuyer_Id(getCurrentUserId())) {
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
        MyUser buyer = _userService.getUserById(getCurrentUserId());
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
            // If the product is already in the cart, just update the quantity
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

        MyUser buyer = _userService.getUserById(getCurrentUserId());
        if(buyer == null) {
            throw new EntityNotFoundException("buyer not found");
        }
        Cart cart = _cartRepo.findCartByBuyer(buyer);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }

        // Use DTOs to avoid lazy loading issues

        return cart.getItems().stream()
                .map(item -> {
                    SellerDto seller = SellerMapper.sellerInProduct(_userService.getUserById(item.getProduct().getSeller().getId()));
                    CartItemProductDto productDto = CartMapper.mapToDTO(item.getProduct(), seller);

//                            Product product =_productService.getFullProductById(item.getProduct().getProductId());
//                            MyUser seller = product.getSeller();
//                            SellerDto sellerDto = SellerMapper.sellerInProduct(seller);
//                            CartItemProductDto productDto = CartMapper.mapToDTO(product, sellerDto);
//                            return CartMapper.mapToDTO(item, productDto);
                    return CartMapper.mapToDTO(item, productDto);

                })
                .toList();
    }
//    public void deleteItemFromCart(UUID CartItemId){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        CostumeUserPrincipal principal = (CostumeUserPrincipal) auth.getPrincipal();
//        UUID userId = principal.getId();
////        System.out.println(((UserPrincipal) auth.getPrincipal()).get);
//    }
}
