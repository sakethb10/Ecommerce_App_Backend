package com.springboot.ecomproj.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.Cart;

@Repository
public interface CartDao extends JpaRepository<Cart, Long>{
	@Query("SELECT c FROM Cart c WHERE c.user.email=:email")
	public Cart findCartByEmail(String email);

	@Query("SELECT c FROM Cart c WHERE c.user.email=:emailId AND c.cartId=:cartId")
	public Cart findCartByEmailAndCartId(String emailId, Long cartId);

	@Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.productId=:productId")
	public List<Cart> findCartsByProductId(Long productId);
}
