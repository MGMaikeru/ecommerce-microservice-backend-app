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
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.service.CartService;

@WebMvcTest(CartResource.class)
public class CartResourceTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CartService cartService;
    
    private CartDto cartDto1;
    private CartDto cartDto2;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        UserDto userDto1 = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .build();
                
        UserDto userDto2 = UserDto.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phone("0987654321")
                .build();
        
        cartDto1 = CartDto.builder()
                .cartId(1)
                .userId(1)
                .userDto(userDto1)
                .build();
                
        cartDto2 = CartDto.builder()
                .cartId(2)
                .userId(2)
                .userDto(userDto2)
                .build();
    }
      @Test
    @DisplayName("Test findAll endpoint returns all carts")
    public void testFindAll() throws Exception {
        // Arrange
        List<CartDto> cartDtos = Arrays.asList(cartDto1, cartDto2);
        when(cartService.findAll()).thenReturn(cartDtos);
        
        // Act & Assert
        mockMvc.perform(get("/api/carts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.collection.size()", is(2)))
            .andExpect(jsonPath("$.collection[0].cartId", is(1)))
            .andExpect(jsonPath("$.collection[0].userId", is(1)))
            .andExpect(jsonPath("$.collection[1].cartId", is(2)))
            .andExpect(jsonPath("$.collection[1].userId", is(2)));
    }
    
    @Test
    @DisplayName("Test findById endpoint returns cart when exists")
    public void testFindById() throws Exception {
        // Arrange
        when(cartService.findById(1)).thenReturn(cartDto1);
        
        // Act & Assert
        mockMvc.perform(get("/api/carts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId", is(1)))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.user.firstName", is("John")));
    }
    
    @Test
    @DisplayName("Test save endpoint creates a new cart")
    public void testSave() throws Exception {
        // Arrange
        when(cartService.save(any(CartDto.class))).thenReturn(cartDto1);
        
        // Act & Assert
        mockMvc.perform(post("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cartDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId", is(1)))
            .andExpect(jsonPath("$.userId", is(1)));
    }
    
    @Test
    @DisplayName("Test update endpoint modifies an existing cart")
    public void testUpdate() throws Exception {
        // Arrange
        when(cartService.update(any(CartDto.class))).thenReturn(cartDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cartDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId", is(1)))
            .andExpect(jsonPath("$.userId", is(1)));
    }
    
    @Test
    @DisplayName("Test update with cartId endpoint modifies an existing cart")
    public void testUpdateWithCartId() throws Exception {
        // Arrange
        when(cartService.update(anyInt(), any(CartDto.class))).thenReturn(cartDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/carts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cartDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId", is(1)))
            .andExpect(jsonPath("$.userId", is(1)));
    }
      @Test
    @DisplayName("Test delete endpoint removes a cart")
    public void testDeleteById() throws Exception {
        // Arrange
        doNothing().when(cartService).deleteById(anyInt());
        
        // Act & Assert
        mockMvc.perform(delete("/api/carts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
    }
}
