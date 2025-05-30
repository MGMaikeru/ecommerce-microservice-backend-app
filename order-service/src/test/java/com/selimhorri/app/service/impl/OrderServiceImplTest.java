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

import java.time.LocalDateTime;
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

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.helper.OrderMappingHelper;
import com.selimhorri.app.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    private Order order1;
    private Order order2;
    private OrderDto orderDto1;
    private Cart cart;
    private CartDto cartDto;
    
    @BeforeEach
    public void setup() {
        // Setup test data
        cart = Cart.builder()
                .cartId(1)
                .userId(1)
                .build();
        
        cartDto = CartDto.builder()
                .cartId(1)
                .userId(1)
                .build();
                
        order1 = Order.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 1")
                .orderFee(100.0)
                .cart(cart)
                .build();
        
        order2 = Order.builder()
                .orderId(2)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 2")
                .orderFee(200.0)
                .cart(cart)
                .build();
        
        orderDto1 = OrderDto.builder()
                .orderId(1)
                .orderDate(order1.getOrderDate())
                .orderDesc("Test Order 1")
                .orderFee(100.0)
                .cartDto(cartDto)
                .build();
    }
    
    @Test
    @DisplayName("Test findAll returns all orders")
    public void testFindAll() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        
        // Act
        List<OrderDto> orders = orderService.findAll();
        
        // Assert
        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test findById returns order when exists")
    public void testFindById_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        
        // Act
        OrderDto result = orderService.findById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals("Test Order 1", result.getOrderDesc());
        assertEquals(100.0, result.getOrderFee());
        verify(orderRepository, times(1)).findById(1);
    }
    
    @Test
    @DisplayName("Test findById throws exception when order does not exist")
    public void testFindById_WhenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(999);
        });
        
        verify(orderRepository, times(1)).findById(999);
    }
    
    @Test
    @DisplayName("Test save persists a new order")
    public void testSave() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order1);
        
        // Act
        OrderDto result = orderService.save(orderDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals("Test Order 1", result.getOrderDesc());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Test update modifies an existing order")
    public void testUpdate() {
        // Arrange
        // Modify the DTO
        orderDto1.setOrderDesc("Updated Order Description");
        orderDto1.setOrderFee(150.0);
        
        // Create updated order to be returned by the mock
        Order updatedOrder = Order.builder()
                .orderId(1)
                .orderDate(order1.getOrderDate())
                .orderDesc("Updated Order Description")
                .orderFee(150.0)
                .cart(cart)
                .build();
        
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        
        // Act
        OrderDto result = orderService.update(orderDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Order Description", result.getOrderDesc());
        assertEquals(150.0, result.getOrderFee());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Test update with orderId modifies an existing order")
    public void testUpdateWithOrderId() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);
        
        // Act
        OrderDto result = orderService.update(1, orderDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Test deleteById removes an order")
    public void testDeleteById() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        doNothing().when(orderRepository).delete(any(Order.class));
        
        // Act
        orderService.deleteById(1);
        
        // Assert
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).delete(any(Order.class));
    }
}
