package com.springboot.ecomproj.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="payments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long paymentId;
	
	@OneToOne(mappedBy="payment", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private Order order;
	
	@NotBlank
	private String paymentMethod;
	
	private String pgPaymentId;
	
	private String pgStatus;
	
	private String pgResponseMessage;
	
	private String pgName;

	public Payment(String paymentMethod, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {
		this.paymentMethod=paymentMethod;
		this.pgPaymentId = pgPaymentId;
		this.pgStatus = pgStatus;
		this.pgResponseMessage = pgResponseMessage;
		this.pgName = pgName;
	}
}
