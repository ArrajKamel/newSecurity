package com.kamel.backend.serivce;

import com.kamel.backend.dto.OrderResponseDto;
import com.kamel.backend.exception.CartIsEmptyException;
import com.kamel.backend.exception.ConcurrencyRetryException;
import com.kamel.backend.exception.StockUnavailableException;
import com.kamel.backend.mapper.OrderMapper;
import com.kamel.backend.model.*;
import com.kamel.backend.repo.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final UserService _userService;
    private final EmailService _emailService;
    private final OrderRepo _orderRepo;
    private final CartService _cartService;
    private final ProductService _productService;

    @Autowired
    public OrderService(UserService userService, EmailService emailService, OrderRepo orderRepo, CartService cartService, ProductService productService) {
        _userService = userService;
        _emailService = emailService;
        _orderRepo = orderRepo;
        _cartService = cartService;
        _productService = productService;
    }

    @Transactional
    public OrderResponseDto placeOrder() {
        MyUser buyer = _userService.getCurrentUser();
        if(buyer == null){
            throw new EntityNotFoundException("User not found");
        }
        Cart cart = _cartService.findByBuyer(buyer);
        if(cart == null) {
            throw new EntityNotFoundException("Cart not found for the buyer");
        }

        List<CartItem> items = cart.getItems();

        if(items == null || items.isEmpty()) {
            throw new CartIsEmptyException();
        }

        final int MAX_RETRIES = 3;
        for (CartItem item : items) {
            int retries;
            // handle changing the quantity locking by optimistic locking
            for(retries = 0; retries < MAX_RETRIES; retries++){
                try {
                    Product product = _productService.findProductById(item.getProduct().getProductId()).orElseThrow(
                            () -> new EntityNotFoundException("Product not found for id " + item.getProduct().getProductId())
                    );

                    int requestedQuantity = item.getQuantity();

                    if (product.getQuantityAvailable() < requestedQuantity) {
                        throw new StockUnavailableException("Insufficient stock for the requested quantity");
                    }

                    product.setQuantityAvailable(product.getQuantityAvailable() - requestedQuantity);
                    _productService.save(product);
                    break;
                } catch (ObjectOptimisticLockingFailureException ex) {
                    if (retries == MAX_RETRIES-1) {
                        throw new RuntimeException("Too many concurrent modifications for product: " + item.getProduct().getProductName(), ex);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ie) {
                        // restore interrupted status
                        Thread.currentThread().interrupt();
                        // wrap in unchecked so we stay in a single transaction boundary
                        throw new ConcurrencyRetryException("Interrupted during retry backoff", ie);
                    }
                }
            }
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for(CartItem item : cart.getItems()) {
            totalPrice = totalPrice.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        //  handle the payment logic, should be before deducting the stock, cuz we mustn't deduct the stock if sth went wrong during the payment
        // and run the order entity creation in a separate thread will be a future feature

        Order order = createOrderEntity(buyer, totalPrice, items);
        // send order confirmation email
        OrderResponseDto orderResponseDto = OrderMapper.mapToDTO(order);
        _emailService.sendOrderConfirmationEmail(orderResponseDto, buyer);

        //clear the cart
        _cartService.deleteCart(cart.getId());

        return orderResponseDto;
    }

    @Transactional
    protected Order createOrderEntity(MyUser buyer, BigDecimal totalPrice, List<CartItem> items){
        Order order = Order.builder()
                .buyer(buyer)
                .totalPrice(totalPrice)
                .build();

        List<OrderItem> orderItems = items.stream()
                .map(cartItem ->
                        OrderItem.builder()
                                .order(order)
                                .product(cartItem.getProduct())
                                .quantity(cartItem.getQuantity())
                                .unitPrice(cartItem.getUnitPrice())
                                .build())
                .toList();

        order.setOrderItems(orderItems);
        order.setStatus(OrderStatus.PLACED);
        return _orderRepo.save(order);

    }
}
