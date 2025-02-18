package com.example.demo.models;

import com.example.demo.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TESTDB.PAYMENT")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    @Column(name = "ID")
	private Long id;
	
	@NotNull
	@Column(name = "ORDER_ID")
	private Long orderId;

    @Min(0)
    @Column(name = "AMOUNT")
	private Double amount;

    @Column(name = "STATUS")
	private PaymentStatus status;
}
