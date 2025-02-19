package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.Payment;
import com.example.demo.repositories.PaymentRepository;

@Service
public class PaymentService {

	@Autowired private PaymentRepository paymentRepository;
	
	public Payment savePayment(Payment payment) {
        payment.setStatus(0);
		payment = paymentRepository.save(payment);
        payment.setStatus(1);
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
