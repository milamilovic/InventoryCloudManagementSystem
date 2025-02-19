package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.Inventory;
import com.example.demo.repositories.InventoryRepository;

@Service
public class InventoryService {

	@Autowired private InventoryRepository inventoryRepository;
	
	public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    public Inventory updateInventory(Long id, Inventory inventoryDetails) {
        Optional<Inventory> inventory = inventoryRepository.findById(id);
        if (inventory.isPresent()) {
            Inventory existingInventory = inventory.get();
            existingInventory.setProductId(inventoryDetails.getProductId());
            existingInventory.setQuantity(inventoryDetails.getQuantity());
            return inventoryRepository.save(existingInventory);
        } else {
            return null;
        }
    }
}
