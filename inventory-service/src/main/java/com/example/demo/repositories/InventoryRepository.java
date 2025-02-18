package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}
