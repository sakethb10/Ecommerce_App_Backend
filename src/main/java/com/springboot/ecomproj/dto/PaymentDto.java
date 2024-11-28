package com.springboot.ecomproj.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentDto {
	private Long paymentId;
	
	@NotBlank
	private String paymentMethod;
	
	private String pgPaymentId;
	
	private String pgStatus;
	
	private String pgResponseMessage;
	
	private String pgName;
}
