package com.springboot.ecomproj.security.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateRequest {
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String password;
	
	private Set<String> role;
}
