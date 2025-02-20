package com.example.demo.contollers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.OrderItem;
import com.example.demo.services.OrderItemService;

@RestController
@RequestMapping("/items")
public class OrderItemController {
	
	@Autowired
	private OrderItemService orderItemService;

    @PostMapping("/create")
    public ResponseEntity<?> addOrderItem(@RequestBody OrderItem orderItem) {
    	try {
	    	OrderItem createdOrderItem = orderItemService.saveOrderItem(orderItem);
	        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    	} catch(IllegalArgumentException e) {
    		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        Optional<OrderItem> orderItem = orderItemService.getOrderItemById(id);
        return orderItem.map(ResponseEntity::ok)
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem updatedOrderItem) {
        OrderItem orderItem = orderItemService.updateOrderItem(id, updatedOrderItem);
        return orderItem != null ? new ResponseEntity<>(orderItem, HttpStatus.OK)
                                 : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        if (orderItemService.getOrderItemById(id).isPresent()) {
            orderItemService.deleteOrderItem(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
