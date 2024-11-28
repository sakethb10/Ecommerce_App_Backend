package com.springboot.ecomproj.service;

import java.util.List;

import com.springboot.ecomproj.dto.OrderDto;

import jakarta.transaction.Transactional;

public interface OrderService {
	@Transactional
	public OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
			String pgStatus, String pgResponseMessage);

	@Transactional
	public List<OrderDto> getAllProducts();

	@Transactional
	public List<OrderDto> getProductsByUserEmail(String emailId);

}
