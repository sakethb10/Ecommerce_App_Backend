package com.springboot.ecomproj.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDto {
	private Long productId;
	
	@NotNull
	private String productName;
	
	private String image;
	
	@NotNull
	private String description;
	
	@NotNull
	@Min (value=0, message="Quantity must be at least zero")
	private Integer quantity;
	
	@NotNull
	@Min (value=0, message="Price must be at least zero")
	private Double price;
	
	private Double discount;
	
	private Double specialPrice;
	
	private String sellerUsername;
}
