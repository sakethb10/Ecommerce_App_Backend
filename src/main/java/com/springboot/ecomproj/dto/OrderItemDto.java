package com.springboot.ecomproj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemDto {
	private Long orderItemId;
	
	private ProductDto product;
	
	private Long orderId;
	
	private Integer quantity;
	
	private Double discount;
	
	private Double orderedProductPrice;
}
