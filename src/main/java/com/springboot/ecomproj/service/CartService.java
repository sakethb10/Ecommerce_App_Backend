package com.springboot.ecomproj.service;

import java.util.List;

import com.springboot.ecomproj.dto.CartDto;

import jakarta.transaction.Transactional;

public interface CartService {

	public CartDto addProductToCart(Long productId, Integer quantity);

	public List<CartDto> getAllCarts();

	public CartDto getCart(String emailId, Long cartId);

	@Transactional
	public CartDto updateProductQuantityInCart(Long productId, Integer quantity);

	@Transactional
	public String deleteProductFromCart(Long cartId, Long productId);

	@Transactional
	public void updateProductInCarts(Long cartId, Long productId);

	@Transactional
	public String clearCart(Long cartId);

}
