package com.springboot.ecomproj.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ecomproj.dao.CartDao;
import com.springboot.ecomproj.dto.CartDto;
import com.springboot.ecomproj.entity.Cart;
import com.springboot.ecomproj.service.CartService;
import com.springboot.ecomproj.util.AuthUtil;

@RestController
@RequestMapping(value="/api")
public class CartController {
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartDao cartDao;
	
	@Autowired
	private AuthUtil authUtil;
	
	@PostMapping(value="/carts/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity){
		CartDto response=cartService.addProductToCart(productId, quantity);
		return new ResponseEntity<>(response, HttpStatus.CREATED); 
	}
	
	@GetMapping(value="/admin/carts")
	public ResponseEntity<List<CartDto>> getCarts(){
		List<CartDto> response=cartService.getAllCarts();
		return new ResponseEntity<>(response, HttpStatus.FOUND);
	}
	
	@GetMapping(value="/carts/users/cart")
	public ResponseEntity<CartDto> getCartById(){
		String emailId=authUtil.loggedInEmail();
		Cart cart=cartDao.findCartByEmail(emailId);
		if(cart==null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		Long cartId=cart.getCartId();
		CartDto response=cartService.getCart(emailId, cartId);
		return new ResponseEntity<>(response, HttpStatus.FOUND);
	}
	
	@PutMapping(value="/carts/products/{productId}/quantity/{operation}")
	public ResponseEntity<CartDto> updateCartProduct(@PathVariable Long productId, @PathVariable String operation){
		CartDto response=cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete")?-1:1);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping(value="/carts/{cartId}/products/{productId}")
	public ResponseEntity<String> deleteCartProduct(@PathVariable Long cartId, @PathVariable Long productId){
		String response=cartService.deleteProductFromCart(cartId, productId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping(value="/carts/{cartId}")
	public ResponseEntity<String> clearCart(@PathVariable Long cartId){
		String response=cartService.clearCart(cartId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
