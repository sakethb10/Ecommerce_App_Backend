package com.springboot.ecomproj.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.springboot.ecomproj.dto.CategoryDto;
import com.springboot.ecomproj.dto.ProductDto;
import com.springboot.ecomproj.exceptions.ResourceNotFoundException;
import com.springboot.ecomproj.payload.ProductResponse;

import jakarta.validation.Valid;

public interface ProductService {

	ProductDto addProduct(ProductDto productDto, String categoryName, String sellerUsername);

	ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductResponse getProductsByCategory(@Valid CategoryDto categoryDto, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductDto updateProduct(Long productId, ProductDto productDto);

	ProductDto deleteProduct(Long productId);

	ProductDto updateProductImage(Long productId, MultipartFile image) throws ResourceNotFoundException, IOException;
	
}
