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
import com.selimhorri.app.service.CategoryService;

@WebMvcTest(CategoryResource.class)
public class CategoryResourceTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CategoryService categoryService;
    
    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        categoryDto1 = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build();
                
        categoryDto2 = CategoryDto.builder()
                .categoryId(2)
                .categoryTitle("Clothing")
                .imageUrl("clothing.jpg")
                .build();
    }
    
    @Test
    @DisplayName("Test findAll endpoint returns all categories")
    public void testFindAll() throws Exception {
        // Arrange
        List<CategoryDto> categoryDtos = Arrays.asList(categoryDto1, categoryDto2);
        when(categoryService.findAll()).thenReturn(categoryDtos);
          // Act & Assert
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.collection.size()", is(2)))
            .andExpect(jsonPath("$.collection[0].categoryId", is(1)))
            .andExpect(jsonPath("$.collection[0].categoryTitle", is("Electronics")))
            .andExpect(jsonPath("$.collection[1].categoryId", is(2)))
            .andExpect(jsonPath("$.collection[1].categoryTitle", is("Clothing")));
    }
    
    @Test
    @DisplayName("Test findById endpoint returns category when exists")
    public void testFindById() throws Exception {
        // Arrange
        when(categoryService.findById(anyInt())).thenReturn(categoryDto1);
        
        // Act & Assert
        mockMvc.perform(get("/api/categories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryId", is(1)))
            .andExpect(jsonPath("$.categoryTitle", is("Electronics")))
            .andExpect(jsonPath("$.imageUrl", is("electronics.jpg")));
    }
    
    @Test
    @DisplayName("Test save endpoint creates a new category")
    public void testSave() throws Exception {
        // Arrange
        when(categoryService.save(any(CategoryDto.class))).thenReturn(categoryDto1);
        
        // Act & Assert
        mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryId", is(1)))
            .andExpect(jsonPath("$.categoryTitle", is("Electronics")))
            .andExpect(jsonPath("$.imageUrl", is("electronics.jpg")));
    }
    
    @Test
    @DisplayName("Test update endpoint modifies an existing category")
    public void testUpdate() throws Exception {
        // Arrange
        when(categoryService.update(any(CategoryDto.class))).thenReturn(categoryDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryId", is(1)))
            .andExpect(jsonPath("$.categoryTitle", is("Electronics")));
    }
    
    @Test
    @DisplayName("Test update with categoryId endpoint modifies an existing category")
    public void testUpdateWithCategoryId() throws Exception {
        // Arrange
        when(categoryService.update(anyInt(), any(CategoryDto.class))).thenReturn(categoryDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryId", is(1)))
            .andExpect(jsonPath("$.categoryTitle", is("Electronics")));
    }
    
    @Test
    @DisplayName("Test delete endpoint removes a category")
    public void testDeleteById() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteById(anyInt());
        
        // Act & Assert
        mockMvc.perform(delete("/api/categories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
    }
}
