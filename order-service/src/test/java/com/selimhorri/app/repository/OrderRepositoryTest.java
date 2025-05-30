package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Test
    @DisplayName("Test save and findById order")
    public void testSaveAndFindById() {
        // Arrange
        // Create cart
        Cart cart = Cart.builder()
                .userId(1)
                .build();
        
        cart = cartRepository.save(cart);
        
        // Create order
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(100.0)
                .cart(cart)
                .build();
        
        // Act
        Order savedOrder = orderRepository.save(order);
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getOrderId());
        
        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals("Test Order", foundOrder.get().getOrderDesc());
        assertEquals(100.0, foundOrder.get().getOrderFee());
        assertNotNull(foundOrder.get().getCart());
        assertEquals(cart.getCartId(), foundOrder.get().getCart().getCartId());
    }
    
    @Test
    @DisplayName("Test update order")
    public void testUpdateOrder() {
        // Arrange
        // Create cart
        Cart cart = Cart.builder()
                .userId(1)
                .build();
        
        cart = cartRepository.save(cart);
        
        // Create and save order
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderDesc("Original Order")
                .orderFee(100.0)
                .cart(cart)
                .build();
        
        order = orderRepository.save(order);
        
        // Act
        // Update order
        order.setOrderDesc("Updated Order");
        order.setOrderFee(150.0);
        Order updatedOrder = orderRepository.save(order);
        
        // Assert
        assertEquals("Updated Order", updatedOrder.getOrderDesc());
        assertEquals(150.0, updatedOrder.getOrderFee());
    }
    
    @Test
    @DisplayName("Test delete order")
    public void testDeleteOrder() {
        // Arrange
        // Create cart
        Cart cart = Cart.builder()
                .userId(1)
                .build();
        
        cart = cartRepository.save(cart);
        
        // Create and save order
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(100.0)
                .cart(cart)
                .build();
        
        order = orderRepository.save(order);
        Integer orderId = order.getOrderId();
        
        // Act
        orderRepository.deleteById(orderId);
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        
        // Assert
        assertTrue(deletedOrder.isEmpty());
    }
}
