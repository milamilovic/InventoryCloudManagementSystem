package com.example.demo.contollers;

import java.util.HashMap;
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

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.models.Payment;
import com.example.demo.services.InventoryService;
import com.example.demo.services.OrderItemService;
import com.example.demo.services.OrderService;
import com.example.demo.services.PaymentService;
import com.example.demo.services.ProductService;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;


@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired private OrderService orderService;
	
	@Autowired private OrderItemService orderItemService;
	
	@Autowired private ProductService productService;
	
	@Autowired private PaymentService paymentService;
	
	@Autowired private InventoryService inventoryService;
	
	@PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.saveOrder(order);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }
	
	@PutMapping("/order/{id}")
    public ResponseEntity<Boolean> placeOrder(@PathVariable Long id) {
        boolean success = true;
		List<OrderItem> orderItems = orderItemService.findByOrderId(id);
		double totalPrice = 0;
		for(OrderItem item : orderItems) {
			double price = getProductPriceWithRetry(item.getProductId());
			if(price <= 0) return new ResponseEntity<>(success, HttpStatus.EXPECTATION_FAILED);
			totalPrice += price;
		}
		HashMap<Long, Integer> changedInventory = new HashMap<>();
		for(OrderItem item : orderItems) {
			Object inventory = changeInventoryWithRetry(item.getProductId(), -1 * item.getQuantity());
			if(inventory == null) {
				success = false;
				//rollback
				for(Long k : changedInventory.keySet()) {
					changeInventoryWithRetry(k, changedInventory.get(k));
				}
		        return new ResponseEntity<>(success, HttpStatus.EXPECTATION_FAILED);
			} else {
				changedInventory.put(item.getProductId(), item.getQuantity());
			}
		}
		success = payOrderWithRetry(new Payment(id, totalPrice));
		
		if(success) return new ResponseEntity<>(success, HttpStatus.OK);
		
		for(Long k : changedInventory.keySet()) {
			changeInventoryWithRetry(k, changedInventory.get(k));
		}
        return new ResponseEntity<>(success, HttpStatus.EXPECTATION_FAILED);
        
    }
	
	@Retry(name = "productService", fallbackMethod = "fallbackGetProductPrice")
	//@Bulkhead(name = "productService", type = Bulkhead.Type.THREADPOOL)
	public double getProductPriceWithRetry(Long productId) {
	    return productService.getProductPrice(productId);
	}

	@Retry(name = "inventoryService", fallbackMethod = "fallbackChangeInventory")
	//@Bulkhead(name = "inventoryService", type = Bulkhead.Type.THREADPOOL)
	public Object changeInventoryWithRetry(Long productId, int quantityChange) {
	    return inventoryService.changeQuantityForProduct(productId, quantityChange);
	}

	@Retry(name = "paymentService", fallbackMethod = "fallbackPayment")
	//@Bulkhead(name = "paymentService", type = Bulkhead.Type.THREADPOOL)
	public boolean payOrderWithRetry(Payment payment) {
	    return paymentService.payOrder(payment) != null;
	}

	public double fallbackGetProductPrice(Long productId, Throwable t) {
		System.out.println("fallback product price");
	    return 0.0; // Fallback price
	}

	public Object fallbackChangeInventory(Long productId, Throwable t) {
		System.out.println("fallback inventory");
	    return null; // Fallback inventory
	}

	public boolean fallbackPayment(Payment payment, Throwable t) {
		System.out.println("fallback payment");
	    return false; // Fallback failed
	}

	public Object fallbackInventory(Long productId, Throwable t) {
		System.out.println("fallback inventory");
	    return null; // Fallback inventory
	}

    @GetMapping("/get-all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
