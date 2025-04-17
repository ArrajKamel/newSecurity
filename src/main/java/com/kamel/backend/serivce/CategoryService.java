package com.kamel.backend.serivce;

import com.kamel.backend.model.Category;
import com.kamel.backend.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepo _categoryRepo;

    @Autowired
    public CategoryService(CategoryRepo categoryRepo) {
        _categoryRepo = categoryRepo;
    }

    public Optional<Category> findByName(String categoryName) {
        return _categoryRepo.findByNameIsIgnoreCase(categoryName);
    }


}
