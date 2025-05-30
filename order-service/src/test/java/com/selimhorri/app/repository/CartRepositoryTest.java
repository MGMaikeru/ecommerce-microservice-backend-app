package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.selimhorri.app.domain.Cart;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;
    
    @Test
    @DisplayName("Test save and findById cart")
    public void testSaveAndFindById() {
        // Arrange
        Cart cart = Cart.builder()
                .userId(1)
                .build();
        
        // Act
        Cart savedCart = cartRepository.save(cart);
        Optional<Cart> foundCart = cartRepository.findById(savedCart.getCartId());
        
        // Assert
        assertTrue(foundCart.isPresent());
        assertEquals(1, foundCart.get().getUserId());
    }
    
    @Test
    @DisplayName("Test update cart")
    public void testUpdateCart() {
        // Arrange
        Cart cart = Cart.builder()
                .userId(1)
                .build();
        
        cart = cartRepository.save(cart);
        
        // Act
        // Update cart
        cart.setUserId(2);
        Cart updatedCart = cartRepository.save(cart);
        
        // Assert
        assertEquals(2, updatedCart.getUserId());
    }
    
    @Test
    @DisplayName("Test delete cart")
    public void testDeleteCart() {
        // Arrange
        Cart cart = Cart.builder()
                .userId(1)
                .build();
        
        cart = cartRepository.save(cart);
        Integer cartId = cart.getCartId();
        
        // Act
        cartRepository.deleteById(cartId);
        Optional<Cart> deletedCart = cartRepository.findById(cartId);
        
        // Assert
        assertTrue(deletedCart.isEmpty());
    }
}
