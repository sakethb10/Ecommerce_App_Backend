package com.springboot.ecomproj.security.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoResponse {
	private Long id;
	
	private String jwtToken;
	
	private String username;
	
	private List<String> roles;
}
