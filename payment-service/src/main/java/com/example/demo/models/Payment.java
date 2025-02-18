package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TESTDB.PAYMENTS")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    @Column(name = "ID")
	private Long id;
	
	@Column(name = "ORDER_ID")
	private Long orderId;

    @Column(name = "AMOUNT")
	private Double amount;

    @Column(name = "STATUS")
	private Integer status;		//-1 as failed, 0 as created and 1 as paid
}
