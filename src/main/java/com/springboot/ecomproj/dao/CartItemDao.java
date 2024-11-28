package com.springboot.ecomproj.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.CartItem;

@Repository
public interface CartItemDao extends JpaRepository<CartItem, Long>{
	@Query("SELECT c FROM CartItem c WHERE c.cart.cartId=:cartId AND c.product.productId=:productId")
	public CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.cart.cartId=:cartId AND c.product.productId=:productId")
	public void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);

}
