package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;

@Service
public class InventoryService {
	
	@Autowired
    private WebClient.Builder webClientBuilder;
	
	@Autowired 
	private CircuitBreaker circuitBreaker;
	
	public Object changeQuantityForProduct(Long productId, int quantity) {
		WebClient webClient = webClientBuilder.baseUrl("http://inventory-service").build();
		return Mono.defer(() -> 
				webClient.put()
		            .uri("/inventories/quantity/" + productId + "/" + quantity)
		            .retrieve()
		            .onStatus(HttpStatusCode::isError, response -> {
		                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
		            	return null;
		            })
		        .bodyToMono(Object.class)
			)
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(throwable -> {
                System.err.println("error circuit breaker: " + throwable.toString());
                return Mono.empty();
            })
        .block();
    }
}
