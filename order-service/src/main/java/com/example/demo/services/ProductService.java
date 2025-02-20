package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductService {
	
	@Autowired
    private WebClient webClient;
	
	public double getProductPrice(Long productId) {
		double price = webClient.get()
	            .uri("/products/price/" + productId)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, response -> {
	                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
	            	return null;
	            })
	            .bodyToMono(Double.class)
	            .block();
        return price;
    }
}
