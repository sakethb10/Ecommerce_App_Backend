package com.springboot.ecomproj.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDto {
	private Long orderId;
	
	@Email
	private String email;
	
	private List<OrderItemDto> orderItems=new ArrayList<>();
	
	private LocalDate orderDate;
	
	private Double totalAmount;
	
	private String orderStatus;
	
	private Long addressId;
}
