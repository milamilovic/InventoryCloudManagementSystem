package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.Product;
import com.example.demo.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired private ProductRepository productRepository;
	
	public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public double getProductPrice(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        
        if (productOptional.isPresent()) {
            return productOptional.get().getPrice();
        }
        return -1;
    }

    /*public Product updateProductQuantity(Long id, int change) {
        Optional<Product> productOptional = productRepository.findById(id);
        
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setQuantity(product.getQuantity() + change);
            return productRepository.save(product);
        }
        
        return null;
    }

    public int getProductQuantity(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        
        if (productOptional.isPresent()) {
            return productOptional.get().getQuantity();
        }
        return -1;	// if the product can't be found
    }*/
}
