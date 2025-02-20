package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.Payment;

@Service
public class PaymentService {

	@Autowired
    private WebClient webClient;
	
	public Payment payOrder(Payment payment) {
		Payment createdPayment = webClient.post()
	            .uri("/payments")
	            .bodyValue(payment)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, response -> {
	                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
	            	return null;
	            })
	            .bodyToMono(Payment.class)
	            .block();
        return createdPayment;
    }
}
