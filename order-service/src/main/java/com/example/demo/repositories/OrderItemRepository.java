package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
	public List<OrderItem> findByOrderId(Long orderId);

}
