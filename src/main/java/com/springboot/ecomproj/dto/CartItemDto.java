package com.springboot.ecomproj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItemDto {
	private Long cartItemId;
	
	private CartDto cart;
	
	private ProductDto product;
	
	private Integer quatity;
	
	private Double discount;
	
	private Double productPrice;
}
