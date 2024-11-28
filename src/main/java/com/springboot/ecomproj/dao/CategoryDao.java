package com.springboot.ecomproj.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.Category;

@Repository
public interface CategoryDao extends JpaRepository<Category, Long>{
	public Category findByCategoryName(String categoryName);
}
