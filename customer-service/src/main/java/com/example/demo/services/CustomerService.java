package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.models.Customer;
import com.example.demo.repositories.CustomerRepository;

@Service
public class CustomerService {

	@Autowired private CustomerRepository customerRepository;
	
	@Autowired private PasswordEncoder passwordEncoder;
    
	@Autowired private AuthenticationManager authenticationManager;
	
	public Customer saveCustomer(Customer customer) {
		customer.setPassword(passwordEncoder.encode(customer.getFirstName()));
        return customerRepository.save(customer);
    }
	
	public Customer login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        return customerRepository.findByEmail(email)
                .orElseThrow();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setFirstName(customerDetails.getFirstName());
            customer.setLastName(customerDetails.getLastName());
            customer.setEmail(customerDetails.getEmail());
            return customerRepository.save(customer);
        } else {
            return null;
        }
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
