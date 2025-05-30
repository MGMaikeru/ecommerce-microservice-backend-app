package com.selimhorri.app.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.service.ProductService;

@WebMvcTest(ProductResource.class)
public class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProductService productService;
    
    private ProductDto productDto1;
    private ProductDto productDto2;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build();
                
        productDto1 = ProductDto.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("smartphone.jpg")
                .sku("SKU-001")
                .priceUnit(599.99)
                .quantity(50)
                .categoryDto(categoryDto)
                .build();
                
        productDto2 = ProductDto.builder()
                .productId(2)
                .productTitle("Laptop")
                .imageUrl("laptop.jpg")
                .sku("SKU-002")
                .priceUnit(999.99)
                .quantity(30)
                .categoryDto(categoryDto)
                .build();
    }
    
    @Test
    @DisplayName("Test findAll endpoint returns all products")
    public void testFindAll() throws Exception {
        // Arrange
        List<ProductDto> productDtos = Arrays.asList(productDto1, productDto2);
        when(productService.findAll()).thenReturn(productDtos);
          // Act & Assert
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.collection.size()", is(2)))
            .andExpect(jsonPath("$.collection[0].productId", is(1)))
            .andExpect(jsonPath("$.collection[0].productTitle", is("Smartphone")))
            .andExpect(jsonPath("$.collection[1].productId", is(2)))
            .andExpect(jsonPath("$.collection[1].productTitle", is("Laptop")));
    }
    
    @Test
    @DisplayName("Test findById endpoint returns product when exists")
    public void testFindById() throws Exception {
        // Arrange
        when(productService.findById(anyInt())).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId", is(1)))
            .andExpect(jsonPath("$.productTitle", is("Smartphone")))
            .andExpect(jsonPath("$.sku", is("SKU-001")))
            .andExpect(jsonPath("$.priceUnit", is(599.99)));
    }
    
    @Test
    @DisplayName("Test save endpoint creates a new product")
    public void testSave() throws Exception {
        // Arrange
        when(productService.save(any(ProductDto.class))).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId", is(1)))
            .andExpect(jsonPath("$.productTitle", is("Smartphone")))
            .andExpect(jsonPath("$.sku", is("SKU-001")));
    }
    
    @Test
    @DisplayName("Test update endpoint modifies an existing product")
    public void testUpdate() throws Exception {
        // Arrange
        when(productService.update(any(ProductDto.class))).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId", is(1)))
            .andExpect(jsonPath("$.productTitle", is("Smartphone")));
    }
    
    @Test
    @DisplayName("Test update with productId endpoint modifies an existing product")
    public void testUpdateWithProductId() throws Exception {
        // Arrange
        when(productService.update(anyInt(), any(ProductDto.class))).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/products/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId", is(1)))
            .andExpect(jsonPath("$.productTitle", is("Smartphone")));
    }
    
    @Test
    @DisplayName("Test delete endpoint removes a product")
    public void testDeleteById() throws Exception {
        // Arrange
        doNothing().when(productService).deleteById(anyInt());
        
        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
    }
}
