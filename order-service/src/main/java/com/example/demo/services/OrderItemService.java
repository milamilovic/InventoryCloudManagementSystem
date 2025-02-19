package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.OrderItem;
import com.example.demo.repositories.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired private OrderItemRepository orderItemRepository;
	
	@Autowired
    private WebClient webClient;
	
	public OrderItem saveOrderItem(OrderItem orderItem) {
		int quantity = webClient.get()
	            .uri("/inventories/quantity/" + orderItem.getProductId())
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, response -> {
	                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
	            	return null;
	            })
	            .bodyToMono(Integer.class)
	            .block();
    	if (orderItem.getQuantity() > quantity) {
            throw new IllegalArgumentException("Not enough quantity for product ID: " + orderItem.getProductId());
        }
        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }

    public OrderItem updateOrderItem(Long id, OrderItem updatedOrderItem) {
        if (orderItemRepository.existsById(id)) {
            updatedOrderItem.setId(id);
            return orderItemRepository.save(updatedOrderItem);
        }
        return null;
    }
}
