package com.springboot.ecomproj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddressDto {
	private Long addressId;
	
	@NotBlank
	private String street;
	
	@NotBlank
	private String buildingName;
	
	@NotBlank
	private String city;
	
	@NotBlank
	private String state;
	
	@NotBlank
	private String country;
	
	@NotBlank
	@Size(min=5, message="Zipcode must be at least 5 numbers long")
	private String zipCode;
}
