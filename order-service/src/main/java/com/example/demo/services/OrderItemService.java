package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.OrderItem;
import com.example.demo.repositories.OrderItemRepository;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;

@Service
public class OrderItemService {

	@Autowired private OrderItemRepository orderItemRepository;
	
	@Autowired
    private WebClient.Builder webClientBuilder;
	
	@Autowired 
	private CircuitBreaker circuitBreaker;
	
	public OrderItem saveOrderItem(OrderItem orderItem) {
		WebClient webClient = webClientBuilder.baseUrl("http://inventory-service").build();

		int quantity = Mono.defer(() -> 
			webClient.get()
	            .uri("/inventories/quantity/" + orderItem.getProductId())
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, response -> {
	                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
	            	return null;
	            })
	            .bodyToMono(Integer.class)
				)
	            .transform(CircuitBreakerOperator.of(circuitBreaker))
	            .onErrorResume(throwable -> {
	                System.err.println("error circuit breaker: " + throwable.toString());
	                return Mono.empty();
	            })
	        .block();
		System.out.println("Quantity: " + quantity);
		if(quantity <= 0 || orderItem.getQuantity() > quantity) {
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

    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
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
