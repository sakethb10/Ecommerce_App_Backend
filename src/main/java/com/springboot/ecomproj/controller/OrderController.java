package com.springboot.ecomproj.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ecomproj.dto.OrderDto;
import com.springboot.ecomproj.dto.OrderRequestDto;
import com.springboot.ecomproj.service.OrderService;
import com.springboot.ecomproj.util.AuthUtil;

@RestController
@RequestMapping(value="/api")
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AuthUtil authUtil;
	
	@PostMapping("/order/users/payments/{paymentMethod}")
	public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDto orderRequestDto){
		String emailId=authUtil.loggedInEmail();
		OrderDto response=orderService.placeOrder(emailId, orderRequestDto.getAddressId(), paymentMethod, orderRequestDto.getPgName(), orderRequestDto.getPgPaymentId(), orderRequestDto.getPgStatus(), orderRequestDto.getPgResponseMessage());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/admin/orders")
	public ResponseEntity<List<OrderDto>> getAllProducts(){
		List<OrderDto> response=orderService.getAllProducts();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/order/users/orders")
	public ResponseEntity<List<OrderDto>> getUserProducts(){
		String emailId=authUtil.loggedInEmail();
		List<OrderDto> response=orderService.getProductsByUserEmail(emailId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
