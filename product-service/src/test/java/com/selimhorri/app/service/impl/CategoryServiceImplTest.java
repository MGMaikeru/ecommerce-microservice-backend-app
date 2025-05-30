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
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.helper.CategoryMappingHelper;
import com.selimhorri.app.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    
    private Category category1;
    private Category category2;
    private CategoryDto categoryDto1;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        category1 = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build();
                
        category2 = Category.builder()
                .categoryId(2)
                .categoryTitle("Clothing")
                .imageUrl("clothing.jpg")
                .build();
                
        categoryDto1 = CategoryMappingHelper.map(category1);
    }
    
    @Test
    @DisplayName("Test findAll returns all categories")
    public void testFindAll() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        
        // Act
        List<CategoryDto> categories = categoryService.findAll();
        
        // Assert
        assertEquals(2, categories.size());
        verify(categoryRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test findById returns category when exists")
    public void testFindById_WhenCategoryExists() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category1));
        
        // Act
        CategoryDto result = categoryService.findById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCategoryId());
        assertEquals("Electronics", result.getCategoryTitle());
        assertEquals("electronics.jpg", result.getImageUrl());
        verify(categoryRepository, times(1)).findById(1);
    }
    
    @Test
    @DisplayName("Test findById throws exception when category does not exist")
    public void testFindById_WhenCategoryDoesNotExist() {
        // Arrange
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.findById(999);
        });
        
        verify(categoryRepository, times(1)).findById(999);
    }
    
    @Test
    @DisplayName("Test save persists a new category")
    public void testSave() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);
        
        // Act
        CategoryDto result = categoryService.save(categoryDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCategoryId());
        assertEquals("Electronics", result.getCategoryTitle());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    @Test
    @DisplayName("Test update modifies an existing category")
    public void testUpdate() {
        // Arrange
        // Modify the DTO
        categoryDto1.setCategoryTitle("Updated Electronics");
        
        // Create updated category to be returned by the mock
        Category updatedCategory = Category.builder()
                .categoryId(1)
                .categoryTitle("Updated Electronics")
                .imageUrl("electronics.jpg")
                .build();
                
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        
        // Act
        CategoryDto result = categoryService.update(categoryDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Electronics", result.getCategoryTitle());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    @Test
    @DisplayName("Test update with categoryId modifies an existing category")
    public void testUpdateWithCategoryId() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);
        
        // Act
        CategoryDto result = categoryService.update(1, categoryDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCategoryId());
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    @Test
    @DisplayName("Test deleteById removes a category")
    public void testDeleteById() {
        // Arrange
        doNothing().when(categoryRepository).deleteById(anyInt());
        
        // Act
        categoryService.deleteById(1);
        
        // Assert
        verify(categoryRepository, times(1)).deleteById(1);
    }
}
