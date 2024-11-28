package com.springboot.ecomproj.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.Product;

@Repository
public interface ProductDao extends JpaRepository<Product,Long>{
	@Query("SELECT p from Product p WHERE p.category.categoryName=:categoryName")
	public Page<Product> getProductsByCategoryName(String categoryName, Pageable pageable);
	
	@Query("SELECT p from Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%',:keyword,'%')) OR p.description LIKE LOWER(CONCAT('%',:keyword,'%')) OR p.category.categoryName LIKE LOWER(CONCAT('%',:keyword,'%'))")
	public Page<Product> getProductsByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
