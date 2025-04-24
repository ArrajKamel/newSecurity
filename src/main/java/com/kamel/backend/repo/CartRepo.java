package com.kamel.backend.repo;

import com.kamel.backend.model.Cart;
import com.kamel.backend.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepo extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByBuyerId(UUID buyerId);

    boolean existsCartByBuyer_Id(UUID buyerId);

    Cart findCartByBuyer(MyUser buyer);
}
