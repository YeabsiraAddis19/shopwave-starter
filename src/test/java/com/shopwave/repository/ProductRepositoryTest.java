// Student Number: ATE/8574/14
package com.shopwave.repository;

import com.shopwave.model.Category;
import com.shopwave.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("Electronics").build();
        entityManager.persist(category);
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatchingProducts() {
        Product p1 = Product.builder()
                .name("Laptop")
                .price(BigDecimal.valueOf(1000))
                .stock(5)
                .category(category)
                .build();
        Product p2 = Product.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(500))
                .stock(10)
                .category(category)
                .build();
        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();

        List<Product> results = productRepository.findByNameContainingIgnoreCase("lap");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptop");
    }
}

