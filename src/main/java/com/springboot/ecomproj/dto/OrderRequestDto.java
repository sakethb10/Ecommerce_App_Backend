package com.springboot.ecomproj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequestDto {
	private Long addressId;
	
	private String paymentMethod;
	
	private String pgName;
	
	private String pgPaymentId;
	
	private String pgStatus;
	
	private String pgResponseMessage;
}
