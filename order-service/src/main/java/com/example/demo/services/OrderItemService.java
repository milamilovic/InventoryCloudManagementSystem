package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.OrderItem;
import com.example.demo.repositories.OrderItemRepository;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class OrderItemService {

	@Autowired private OrderItemRepository orderItemRepository;

	@Autowired private InventoryService inventoryService;
	
	public OrderItem saveOrderItem(OrderItem orderItem) {
	    int quantity = getQuantityForProduct(orderItem.getProductId());

	    if(quantity <= 0 || orderItem.getQuantity() > quantity) {
	        throw new IllegalArgumentException("Not enough quantity for product ID: " + orderItem.getProductId());
	    }
	    return orderItemRepository.save(orderItem);
	}

	@Retry(name = "inventoryService", fallbackMethod = "fallbackInventory")
	//@Bulkhead(name = "inventoryService", type = Bulkhead.Type.THREADPOOL)
	public int getQuantityForProduct(Long productId) {
	    return inventoryService.getQuantityForProduct(productId);
	}

	public int fallbackInventory(Long productId, Throwable t) {
	    return 0;
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
