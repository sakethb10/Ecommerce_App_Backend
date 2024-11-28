package com.springboot.ecomproj.service;

import com.springboot.ecomproj.dto.CategoryDto;
import com.springboot.ecomproj.payload.CategoryResponse;

public interface CategoryService{
	public CategoryDto createCategory(CategoryDto dto);
	
	public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	public CategoryDto updateCategory(String categoryName, CategoryDto updatedCategory);
	
	public CategoryDto deleteCategory(String categoryName);
}
