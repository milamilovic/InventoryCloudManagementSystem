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

import com.example.demo.models.Inventory;
import com.example.demo.services.InventoryService;

@RestController
@RequestMapping("/inventories")
public class InventoryController {

	@Autowired private InventoryService inventoryService;
	
	@PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        Inventory savedInventory = inventoryService.saveInventory(inventory);
        return ResponseEntity.ok(savedInventory);
    }

    @GetMapping("/find-all")
    public List<Inventory> getAllInventories() {
        return inventoryService.getAllInventories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryService.getInventoryById(id);
        return inventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory inventoryDetails) {
        Inventory updatedInventory = inventoryService.updateInventory(id, inventoryDetails);
        return updatedInventory != null ? ResponseEntity.ok(updatedInventory) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/quantity/{id}/{change}")
    public ResponseEntity<Inventory> updateProductQuantity(@PathVariable Long id, @PathVariable int change) {
    	Inventory updatedInventory = inventoryService.updateProductQuantityByProductId(id, change);
        
        if (updatedInventory != null) {
            return ResponseEntity.ok(updatedInventory);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/quantity/{id}")
    public ResponseEntity<Integer> getProductQuantity(@PathVariable Long id) {
		System.out.println("pogodio get quantity inventory");
        int quantity = inventoryService.getQuantityByProductId(id);
        
        if (quantity != -1) {
            return ResponseEntity.ok(quantity);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
