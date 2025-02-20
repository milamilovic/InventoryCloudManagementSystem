package com.example.demo.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
            .timeout(Duration.ofSeconds(7))
            .onErrorResume(throwable -> {
                System.err.println("error circuit breaker: " + throwable.toString());
                return Mono.empty();
            })
        .block();
    }
	
	public int getQuantityForProduct(Long productId) {
        WebClient webClient = webClientBuilder.baseUrl("http://inventory-service").build();
        
        return Mono.defer(() -> 
            webClient.get()
                .uri("/inventories/quantity/" + productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    System.out.println("Error response: " + response.statusCode());
                    return Mono.error(new IllegalStateException("Service unavailable"));
                })
                .bodyToMono(Integer.class)
        )
        .transform(CircuitBreakerOperator.of(circuitBreaker))
        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
        .timeout(Duration.ofSeconds(7))
        .onErrorResume(throwable -> {
            System.err.println("Error while getting product quantity: " + throwable.toString());
            return Mono.just(0);
        })
        .block();
    }
}
