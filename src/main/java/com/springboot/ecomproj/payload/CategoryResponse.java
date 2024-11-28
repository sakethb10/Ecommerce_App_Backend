package com.springboot.ecomproj.payload;

import java.util.List;

import com.springboot.ecomproj.dto.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryResponse {
	private List<CategoryDto> content;
	
	private Integer pageNumber;
	
	private Integer pageSize;
	
	private Long totalElements;
	
	private Integer totalPages;
	
	private Boolean lastPage;
}
