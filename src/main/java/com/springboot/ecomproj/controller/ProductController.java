package com.springboot.ecomproj.controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.ecomproj.config.AppConstants;
import com.springboot.ecomproj.dto.CategoryDto;
import com.springboot.ecomproj.dto.ProductDto;
import com.springboot.ecomproj.payload.ProductResponse;
import com.springboot.ecomproj.service.ProductService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping (value="/api")
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@PostMapping (value="/seller/admin/categories/{categoryName}/product")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
	public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productDto, @PathVariable String categoryName, @RequestParam(name="sellerUsername", required=true) String sellerUsername){
		ProductDto newProduct=productService.addProduct(productDto, categoryName, sellerUsername);
		return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
	}
	
	@GetMapping (value="/public/products")
	public ResponseEntity<ProductResponse> getAllProducts(@RequestParam (name="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=false) Integer pageNumber, @RequestParam (name="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=false) Integer pageSize, @RequestParam(name="sortBy", defaultValue=AppConstants.SORT_PRODUCTS_BY, required=false) String sortBy, @RequestParam(name="sortOrder", defaultValue=AppConstants.SORT_DIR, required=false) String sortOrder){
		ProductResponse productResponse=productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
	@GetMapping (value="/public/categories/products")
	public ResponseEntity<ProductResponse> getProductsByCategory(@Valid @RequestBody CategoryDto categoryDto, @RequestParam (name="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=false) Integer pageNumber, @RequestParam (name="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=false) Integer pageSize, @RequestParam(name="sortBy", defaultValue=AppConstants.SORT_PRODUCTS_BY, required=false) String sortBy, @RequestParam(name="sortOrder", defaultValue=AppConstants.SORT_DIR, required=false) String sortOrder){
		ProductResponse productResponse=productService.getProductsByCategory(categoryDto, pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
	@GetMapping (value="/public/products/keyword/{keyword}")
	public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword, @RequestParam (name="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=false) Integer pageNumber, @RequestParam (name="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=false) Integer pageSize, @RequestParam(name="sortBy", defaultValue=AppConstants.SORT_PRODUCTS_BY, required=false) String sortBy, @RequestParam(name="sortOrder", defaultValue=AppConstants.SORT_DIR, required=false) String sortOrder){
		ProductResponse productResponse=productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
	@PutMapping (value="/seller/admin/products/updateProduct/{productId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
	public ResponseEntity<ProductDto> updatedProduct(@PathVariable Long productId, @Valid @RequestBody ProductDto productDto, @RequestParam(name="sellerUsername", required=true) String sellerUsername){
		ProductDto updatedProduct=productService.updateProduct(productId, productDto);
		return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}
	
	@DeleteMapping (value="/seller/admin/products/deleteProduct/{productId}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
	public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long productId, @RequestParam(name="sellerUsername", required=true) String sellerUsername){
		ProductDto deletedProduct=productService.deleteProduct(productId);
		return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
	}
	
	@PutMapping (value="/seller/admin/products/{productId}/image")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
	public ResponseEntity<ProductDto> updateProductImage(@PathVariable Long productId, @RequestParam(name="image") MultipartFile image, @RequestParam(name="sellerUsername", required=true) String sellerUsername) throws IOException{
		ProductDto updatedProduct=productService.updateProductImage(productId, image);
		return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}
}
