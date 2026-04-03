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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductDTO productDTO;
    private CreateProductRequest request;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").build();
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .stock(10)
                .category(category)
                .build();
        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .stock(10)
                .categoryId(1L)
                .categoryName("Electronics")
                .build();
        request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(BigDecimal.valueOf(999.99));
        request.setStock(10);
        request.setCategoryId(1L);
    }

    @Test
    void createProduct_happyPath_returnsProductDTO() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(request);

        assertThat(result).isEqualTo(productDTO);
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toDto(product);
    }

    @Test
    void createProduct_categoryNotFound_throwsIllegalArgumentException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void getProductById_notFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStock_negativeDelta_throwsIllegalArgumentException() {
        product.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(1L, -10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock cannot be negative");
    }
}

