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

import com.example.demo.models.Order;
import com.example.demo.models.OrderItem;
import com.example.demo.models.Payment;
import com.example.demo.services.InventoryService;
import com.example.demo.services.OrderItemService;
import com.example.demo.services.OrderService;
import com.example.demo.services.PaymentService;
import com.example.demo.services.ProductService;

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
    public ResponseEntity<Boolean> order(@PathVariable Long id) {
        boolean success = true;
		List<OrderItem> orderItems = orderItemService.findByOrderId(id);
		double totalPrice = 0;
		for(OrderItem item : orderItems) {
			totalPrice += productService.getProductPrice(item.getProductId());
		}
		paymentService.payOrder(new Payment(id, totalPrice));
		for(OrderItem item : orderItems) {
			inventoryService.changeQuantityForProduct(item.getProductId(), item.getQuantity());
		}
        return new ResponseEntity<>(success, HttpStatus.OK);
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
