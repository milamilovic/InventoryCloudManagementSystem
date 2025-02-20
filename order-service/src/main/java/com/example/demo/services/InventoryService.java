package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InventoryService {
	
	@Autowired
    private WebClient webClient;
	
	public Object changeQuantityForProduct(Long productId, int quantity) {
		Object price = webClient.put()
	            .uri("/inventories/quantity/" + productId + "/" + quantity)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, response -> {
	                System.out.println("Error response: " + response.statusCode() + " - " + response.toString());
	            	return null;
	            })
	            .bodyToMono(Object.class)
	            .block();
        return price;
    }
}
