package com.springboot.ecomproj.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartDto {
	private Long cartId;
	
	private Double totalPrice;
	
	private List<ProductDto> products;
}
