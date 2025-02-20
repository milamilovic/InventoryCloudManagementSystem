package com.example.demo.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.Payment;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class PaymentService {

	@Autowired
    private WebClient.Builder webClientBuilder;
	
	@Autowired 
	private CircuitBreaker circuitBreaker;
	
	public Payment payOrder(Payment payment) {
		WebClient webClient = webClientBuilder.baseUrl("http://payment-service").build();
		return Mono.defer(() -> 
        		webClient.post()
		            .uri("/payments")
		            .bodyValue(payment)
		            .retrieve()
		            .onStatus(HttpStatusCode::isError, response -> {
		                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
		            	return null;
	            })
	            .bodyToMono(Payment.class)
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
}
