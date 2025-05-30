package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CartNotFoundException;
import com.selimhorri.app.repository.CartRepository;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private CartServiceImpl cartService;
    
    private Cart cart1;
    private Cart cart2;
    private CartDto cartDto1;
    private UserDto userDto;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        cart1 = Cart.builder()
                .cartId(1)
                .userId(1)
                .build();
                
        cart2 = Cart.builder()
                .cartId(2)
                .userId(2)
                .build();
        
        userDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .build();
                
        cartDto1 = CartDto.builder()
                .cartId(1)
                .userId(1)
                .userDto(userDto)
                .build();
    }
    
    @Test
    @DisplayName("Test findAll returns all carts")
    public void testFindAll() {
        // Arrange
        when(cartRepository.findAll()).thenReturn(Arrays.asList(cart1, cart2));
        when(restTemplate.getForObject(anyString(), any())).thenReturn(userDto);
        
        // Act
        List<CartDto> carts = cartService.findAll();
        
        // Assert
        assertEquals(2, carts.size());
        verify(cartRepository, times(1)).findAll();
        verify(restTemplate, times(2)).getForObject(anyString(), any());
    }
    
    @Test
    @DisplayName("Test findById returns cart when exists")
    public void testFindById_WhenCartExists() {
        // Arrange
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart1));
        when(restTemplate.getForObject(anyString(), any())).thenReturn(userDto);
        
        // Act
        CartDto result = cartService.findById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(1, result.getUserId());
        assertNotNull(result.getUserDto());
        assertEquals("John", result.getUserDto().getFirstName());
        verify(cartRepository, times(1)).findById(1);
        verify(restTemplate, times(1)).getForObject(anyString(), any());
    }
    
    @Test
    @DisplayName("Test findById throws exception when cart does not exist")
    public void testFindById_WhenCartDoesNotExist() {
        // Arrange
        when(cartRepository.findById(anyInt())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> {
            cartService.findById(999);
        });
        
        verify(cartRepository, times(1)).findById(999);
    }
    
    @Test
    @DisplayName("Test save persists a new cart")
    public void testSave() {
        // Arrange
        when(cartRepository.save(any(Cart.class))).thenReturn(cart1);
        
        // Act
        CartDto result = cartService.save(cartDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(1, result.getUserId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    
    @Test
    @DisplayName("Test update modifies an existing cart")
    public void testUpdate() {
        // Arrange
        // Modify the DTO
        cartDto1.setUserId(3);
        
        // Create updated cart to be returned by the mock
        Cart updatedCart = Cart.builder()
                .cartId(1)
                .userId(3)
                .build();
        
        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);
        
        // Act
        CartDto result = cartService.update(cartDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(3, result.getUserId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    
    @Test
    @DisplayName("Test update with cartId modifies an existing cart")
    public void testUpdateWithCartId() {
        // Arrange
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart1));
        when(restTemplate.getForObject(anyString(), any())).thenReturn(userDto);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart1);
        
        // Act
        CartDto result = cartService.update(1, cartDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        verify(cartRepository, times(1)).findById(1);
        verify(restTemplate, times(1)).getForObject(anyString(), any());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    
    @Test
    @DisplayName("Test deleteById removes a cart")
    public void testDeleteById() {
        // Arrange
        doNothing().when(cartRepository).deleteById(anyInt());
        
        // Act
        cartService.deleteById(1);
        
        // Assert
        verify(cartRepository, times(1)).deleteById(1);
    }
}
