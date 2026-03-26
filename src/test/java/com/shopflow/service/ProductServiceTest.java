package com.shopflow.service;

import com.shopflow.dto.request.ProductRequest;
import com.shopflow.dto.response.ProductResponse;
import com.shopflow.entity.Category;
import com.shopflow.entity.Product;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // @Mock creates a fake version of the repository
    // It does nothing by default — you tell it what to return per test
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    // @InjectMocks creates a real ProductService
    // and injects the mocks above into its constructor
    @InjectMocks
    private ProductService productService;

    // Test data we reuse across tests
    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Runs before EACH test — fresh data every time
        testCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic products")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Latest iPhone")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .imageUrl("iphone.jpg")
                .category(testCategory)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should return all active products")
    void getAllProducts_ShouldReturnActiveProducts() {

        // ARRANGE — tell the mock what to return
        when(productRepository.findByActiveTrue())
                .thenReturn(List.of(testProduct));

        // ACT — call the actual service method
        List<ProductResponse> result = productService.getAllProducts();

        // ASSERT — verify the result is correct
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");
        assertThat(result.get(0).getPrice())
                .isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(result.get(0).getCategoryName()).isEqualTo("Electronics");

        // Verify the repository was actually called once
        verify(productRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should return product when found by id")
    void getProductById_WhenProductExists_ShouldReturnProduct() {

        // ARRANGE
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(testProduct));

        // ACT
        ProductResponse result = productService.getProductById(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("iPhone 15");
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void getProductById_WhenProductNotFound_ShouldThrowException() {

        // ARRANGE — mock returns empty Optional (product doesn't exist)
        when(productRepository.findByIdAndActiveTrue(99L))
                .thenReturn(Optional.empty());

        // ASSERT + ACT — verify the exception is thrown with correct message
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 99");
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_WhenCategoryExists_ShouldReturnCreatedProduct() {

        // ARRANGE
        ProductRequest request = new ProductRequest();
        request.setName("Samsung Galaxy");
        request.setDescription("Latest Samsung");
        request.setPrice(new BigDecimal("799.99"));
        request.setStockQuantity(30);
        request.setCategoryId(1L);

        Product savedProduct = Product.builder()
                .id(2L)
                .name("Samsung Galaxy")
                .description("Latest Samsung")
                .price(new BigDecimal("799.99"))
                .stockQuantity(30)
                .category(testCategory)
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // ACT
        ProductResponse result = productService.createProduct(request);

        // ASSERT
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Samsung Galaxy");
        assertThat(result.getCategoryName()).isEqualTo("Electronics");

        // Verify save was called exactly once with any Product argument
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should soft delete product by setting active to false")
    void deleteProduct_WhenProductExists_ShouldSetActiveFalse() {

        // ARRANGE
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct);

        // ACT
        productService.deleteProduct(1L);

        // ASSERT — capture what was passed to save()
        // and verify active was set to false
        verify(productRepository, times(1)).save(
                argThat(product -> !product.getActive())
        );
    }
}