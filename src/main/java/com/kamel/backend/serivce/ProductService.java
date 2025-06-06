package com.kamel.backend.serivce;

import com.kamel.backend.dto.CreateProductRequest;
import com.kamel.backend.dto.ProductResponse;
import com.kamel.backend.dto.SellerDto;
import com.kamel.backend.dto.UpdateProductRequest;
import com.kamel.backend.mapper.ProductMapper;
import com.kamel.backend.mapper.SellerMapper;
import com.kamel.backend.model.Category;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.model.Product;
import com.kamel.backend.repo.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepo _productRepo;
    private final UserService _userService;
    private final CategoryService _categoryService;
    @Autowired
    public ProductService(ProductRepo productRepo,
                          UserService userService,
                          CategoryService categoryService) {
        _productRepo = productRepo;
        _userService = userService;
        _categoryService = categoryService;
    }

    public Product createProduct(CreateProductRequest productDto) {
        MyUser seller = _userService.getUserById(_userService.getCurrentUserId());
        if(seller == null) {
            throw new EntityNotFoundException("seller not found");
        }

        Category category = _categoryService.findByName(productDto.getCategoryName()).orElseThrow(
                () -> new EntityNotFoundException("Category not found")
        );

        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .brand(productDto.getBrand())
                .price(productDto.getPrice())
                .productName(productDto.getName())
                .description(productDto.getDescription())
                .year(productDto.getYear())
                .fuelType(productDto.getFuelType())
                .mileageKm(productDto.getMileageKm())
                .engineCapacityCC(productDto.getEngineCapacityCC())
                .transmission(productDto.getTransmission())
                .model(productDto.getModel())
                .quantityAvailable(productDto.getQuantityAvailable())
                .used(productDto.isUsed())
                .build();

        return _productRepo.save(product);
    }

    public List<ProductResponse> findAll() {
        return _productRepo.findAll().stream()
                .map(product -> {
                    SellerDto seller = SellerMapper.sellerInProduct(_userService.getUserById(product.getSeller().getId()));
                    return ProductMapper.mapToDTO(product, seller);
                })
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(UUID productId) {
        Product product =  _productRepo.findById(productId)
                .orElseThrow( () -> new EntityNotFoundException("product not found"));
        SellerDto seller = SellerMapper.sellerInProduct(_userService.getUserById(product.getSeller().getId()));
        return ProductMapper.mapToDTO(product, seller);
    }
    public Optional<Product> findProductById(UUID productId) {
        return _productRepo.findById(productId);
    }

    public Product getFullProductById(UUID productId) {  // do not use this method in the controller, it is meant for adding a n item to the cart in cart service
        return _productRepo.findById(productId).orElseThrow( () -> new EntityNotFoundException("product not found"));
    }

    public void deleteProduct(UUID productId) {
        MyUser seller = _userService.getUserById(_userService.getCurrentUserId());
        if(seller == null) {
            throw new EntityNotFoundException("seller not found");
        }
        List<Product> sellerProduct = _productRepo.findAllBySeller(seller);

        Product product = _productRepo.findById(productId)
                .orElseThrow( () -> new EntityNotFoundException("product not found"));

        boolean ownsProduct = sellerProduct.stream()
                .anyMatch(p -> p.getProductId().equals(productId));

        if(!ownsProduct) {
//            System.out.println("am i getting here");
            throw new EntityNotFoundException("this product is not yours");
        }
        System.out.println("the product is yours");

        _productRepo.deleteById(productId);
    }

    public ProductResponse updateProductPartially(UUID productId, UpdateProductRequest patchDto) {
        Product product = _productRepo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (patchDto.getProductName() != null) product.setProductName(patchDto.getProductName());
        if (patchDto.getDescription() != null) product.setDescription(patchDto.getDescription());
        if (patchDto.getPrice() != null) product.setPrice(patchDto.getPrice());
        if (patchDto.getQuantityAvailable() != null) product.setQuantityAvailable(patchDto.getQuantityAvailable());
        if (patchDto.getBrand() != null) product.setBrand(patchDto.getBrand());
        if (patchDto.getModel() != null) product.setModel(patchDto.getModel());
        if (patchDto.getYear() != null) product.setYear(patchDto.getYear());
        if (patchDto.getEngineCapacityCC() != null) product.setEngineCapacityCC(patchDto.getEngineCapacityCC());
        if (patchDto.getFuelType() != null) product.setFuelType(patchDto.getFuelType());
        if (patchDto.getTransmission() != null) product.setTransmission(patchDto.getTransmission());
        if (patchDto.getMileageKm() != null) product.setMileageKm(patchDto.getMileageKm());
        if(patchDto.getUsed() != null) {product.setUsed(patchDto.getUsed());}
        if (patchDto.getCategoryName() != null) {
            Category category = _categoryService.findByName(patchDto.getCategoryName())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        Product updatedProduct =  _productRepo.save(product);
        SellerDto seller = SellerMapper.sellerInProduct(_userService.getUserById(product.getSeller().getId()));
        return ProductMapper.mapToDTO(updatedProduct, seller);
    }

    /**
     *
     * @param productId
     * @param quantity
     * @return the new quantity available after the update
     */
    protected int increaseProductQuantity(UUID productId, int quantity) {
        Product product = _productRepo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
        return product.getQuantityAvailable();
    }

    protected void save(Product product){
        _productRepo.save(product);
    }

}
