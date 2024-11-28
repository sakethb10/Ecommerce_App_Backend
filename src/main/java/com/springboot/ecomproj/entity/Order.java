package com.springboot.ecomproj.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long orderId;
	
	@Email
	@Column(name="email", nullable=false)
	private String email;
	
	@OneToMany(mappedBy="order", cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	private List<OrderItem> orderItems;
	
	private LocalDate orderDate;
	
	@OneToOne
	@JoinColumn(name="payment_id")
	private Payment payment;
	
	private Double totalAmount;
	
	private String orderStatus;
	
	@ManyToOne
	@JoinColumn(name="address_id")
	private Address address;
}
