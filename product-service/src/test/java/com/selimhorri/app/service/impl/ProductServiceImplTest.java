package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.helper.ProductMappingHelper;
import com.selimhorri.app.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private Category category;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build();
                
        product1 = Product.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("smartphone.jpg")
                .sku("SKU-001")
                .priceUnit(599.99)
                .quantity(50)
                .category(category)
                .build();
        
        product2 = Product.builder()
                .productId(2)
                .productTitle("Laptop")
                .imageUrl("laptop.jpg")
                .sku("SKU-002")
                .priceUnit(999.99)
                .quantity(30)
                .category(category)
                .build();
                
        productDto1 = ProductMappingHelper.map(product1);
    }
    
    @Test
    @DisplayName("Test findAll returns all products")
    public void testFindAll() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        // Act
        List<ProductDto> products = productService.findAll();
        
        // Assert
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test findById returns product when exists")
    public void testFindById_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        
        // Act
        ProductDto result = productService.findById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("Smartphone", result.getProductTitle());
        assertEquals("smartphone.jpg", result.getImageUrl());
        assertEquals("SKU-001", result.getSku());
        assertEquals(599.99, result.getPriceUnit());
        assertEquals(50, result.getQuantity());
        verify(productRepository, times(1)).findById(1);
    }
    
    @Test
    @DisplayName("Test findById throws exception when product does not exist")
    public void testFindById_WhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.findById(999);
        });
        
        verify(productRepository, times(1)).findById(999);
    }
    
    @Test
    @DisplayName("Test save persists a new product")
    public void testSave() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        
        // Act
        ProductDto result = productService.save(productDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("Smartphone", result.getProductTitle());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test update modifies an existing product")
    public void testUpdate() {
        // Arrange
        // Modify the DTO
        productDto1.setProductTitle("Updated Smartphone");
        productDto1.setPriceUnit(649.99);
        
        // Create updated product to be returned by the mock
        Product updatedProduct = Product.builder()
                .productId(1)
                .productTitle("Updated Smartphone")
                .imageUrl("smartphone.jpg")
                .sku("SKU-001")
                .priceUnit(649.99)
                .quantity(50)
                .category(category)
                .build();
                
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        
        // Act
        ProductDto result = productService.update(productDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Smartphone", result.getProductTitle());
        assertEquals(649.99, result.getPriceUnit());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test update with productId modifies an existing product")
    public void testUpdateWithProductId() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        
        // Act
        ProductDto result = productService.update(1, productDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test deleteById removes a product")
    public void testDeleteById() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        
        // Act
        productService.deleteById(1);
        
        // Assert
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(any(Product.class));
    }
}
