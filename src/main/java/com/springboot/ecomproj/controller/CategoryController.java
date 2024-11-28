package com.springboot.ecomproj.controller;

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

import com.springboot.ecomproj.config.AppConstants;
import com.springboot.ecomproj.dto.CategoryDto;
import com.springboot.ecomproj.payload.CategoryResponse;
import com.springboot.ecomproj.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @PostMapping(value = "/admin/category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto request) {
        CategoryDto result = categoryService.createCategory(request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @GetMapping(value = "/public/category")
    public ResponseEntity<CategoryResponse> getCategories(@RequestParam(name="pageNumber", defaultValue=AppConstants.PAGE_NUMBER, required=false) Integer pageNumber, @RequestParam(name="pageSize", defaultValue=AppConstants.PAGE_SIZE, required=false) Integer pageSize, @RequestParam(name="sortBy", defaultValue=AppConstants.SORT_CATEGORIES_BY, required=false) String sortBy, @RequestParam(name="sortOrder", defaultValue=AppConstants.SORT_DIR, required=false) String sortOrder) {
        CategoryResponse result = categoryService.getCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @PutMapping(value = "/admin/category/{categoryName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryName, @Valid @RequestBody CategoryDto updatedCategory) {
        CategoryDto result = categoryService.updateCategory(categoryName, updatedCategory);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @DeleteMapping(value = "/admin/category/{categoryName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable String categoryName) {
        CategoryDto result=categoryService.deleteCategory(categoryName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
