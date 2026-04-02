// Student Number: ATE/8574/14
package com.shopwave.service;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.mapper.ProductMapper;
import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductDTO createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String keyword, BigDecimal maxPrice) {
        List<Product> products;
        if (keyword != null && !keyword.isBlank() && maxPrice != null) {
            products = productRepository.findByNameContainingIgnoreCase(keyword)
                    .stream()
                    .filter(p -> p.getPrice().compareTo(maxPrice) <= 0)
                    .toList();
        } else if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (maxPrice != null) {
            products = productRepository.findByPriceLessThanEqual(maxPrice);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDTO updateStock(Long id, int delta) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        int newStock = product.getStock() + delta;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        product.setStock(newStock);
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }
}

