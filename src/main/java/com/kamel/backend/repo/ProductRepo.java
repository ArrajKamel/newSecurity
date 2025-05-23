package com.kamel.backend.repo;

import com.kamel.backend.model.MyUser;
import com.kamel.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {

    List<Product> findAllBySeller(MyUser seller);
}
