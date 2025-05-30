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

import java.time.LocalDateTime;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.service.OrderService;

@WebMvcTest(OrderResource.class)
public class OrderResourceTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    private ObjectMapper objectMapper;
    
    private OrderDto orderDto1;
    private OrderDto orderDto2;
    
    @BeforeEach
    public void setup() {
        // Setup ObjectMapper with JavaTimeModule for LocalDateTime serialization
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Setup test data
        CartDto cartDto = CartDto.builder()
                .cartId(1)
                .userId(1)
                .build();
        
        orderDto1 = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 1")
                .orderFee(100.0)
                .cartDto(cartDto)
                .build();
        
        orderDto2 = OrderDto.builder()
                .orderId(2)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 2")
                .orderFee(200.0)
                .cartDto(cartDto)
                .build();
    }
      @Test
    @DisplayName("Test findAll endpoint returns all orders")
    public void testFindAll() throws Exception {
        // Arrange
        List<OrderDto> orderDtos = Arrays.asList(orderDto1, orderDto2);
        when(orderService.findAll()).thenReturn(orderDtos);
        
        // Act & Assert
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.collection.size()", is(2)))
            .andExpect(jsonPath("$.collection[0].orderId", is(1)))
            .andExpect(jsonPath("$.collection[0].orderDesc", is("Test Order 1")))
            .andExpect(jsonPath("$.collection[1].orderId", is(2)))
            .andExpect(jsonPath("$.collection[1].orderDesc", is("Test Order 2")));
    }
    
    @Test
    @DisplayName("Test findById endpoint returns order when exists")
    public void testFindById() throws Exception {
        // Arrange
        when(orderService.findById(1)).thenReturn(orderDto1);
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId", is(1)))
            .andExpect(jsonPath("$.orderDesc", is("Test Order 1")))
            .andExpect(jsonPath("$.orderFee", is(100.0)));
    }
    
    @Test
    @DisplayName("Test save endpoint creates a new order")
    public void testSave() throws Exception {
        // Arrange
        when(orderService.save(any(OrderDto.class))).thenReturn(orderDto1);
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId", is(1)))
            .andExpect(jsonPath("$.orderDesc", is("Test Order 1")))
            .andExpect(jsonPath("$.orderFee", is(100.0)));
    }
    
    @Test
    @DisplayName("Test update endpoint modifies an existing order")
    public void testUpdate() throws Exception {
        // Arrange
        when(orderService.update(any(OrderDto.class))).thenReturn(orderDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId", is(1)))
            .andExpect(jsonPath("$.orderDesc", is("Test Order 1")));
    }
    
    @Test
    @DisplayName("Test update with orderId endpoint modifies an existing order")
    public void testUpdateWithOrderId() throws Exception {
        // Arrange
        when(orderService.update(anyInt(), any(OrderDto.class))).thenReturn(orderDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/orders/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId", is(1)))
            .andExpect(jsonPath("$.orderDesc", is("Test Order 1")));
    }
      @Test
    @DisplayName("Test delete endpoint removes an order")
    public void testDeleteById() throws Exception {
        // Arrange
        doNothing().when(orderService).deleteById(anyInt());
        
        // Act & Assert
        mockMvc.perform(delete("/api/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
    }
}
