package com.kamel.backend.controller;

import com.kamel.backend.dto.CreateProductRequest;
import com.kamel.backend.dto.ProductResponse;
import com.kamel.backend.dto.UpdateProductRequest;
import com.kamel.backend.model.Product;
import com.kamel.backend.serivce.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@CrossOrigin
public class ProductController {
    private final ProductService _productService;

    @Autowired
    public ProductController(ProductService productService) {
        _productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest productDto) {
        try {
            Product product = _productService.createProduct(productDto);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return new ResponseEntity<>(_productService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId) {
        try {
            ProductResponse product = _productService.getProdctById(productId);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId) {
        try {
            _productService.deleteProduct(productId);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/update/{productId}")
    public ResponseEntity<?> patchProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest patchDto
    ) {
        try {
            ProductResponse updated = _productService.updateProductPartially(productId, patchDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
