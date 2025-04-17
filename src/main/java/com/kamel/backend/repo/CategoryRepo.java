package com.kamel.backend.repo;

import com.kamel.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepo extends JpaRepository<Category, UUID> {

    Optional<Category> findByNameIsIgnoreCase(String categoryName);
}
