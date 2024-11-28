package com.springboot.ecomproj.payload;

import java.util.List;

import org.springframework.stereotype.Component;

import com.springboot.ecomproj.dto.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse {
	private List<ProductDto> content;
	
	private Integer pageNumber;
	
	private Integer pageSize;
	
	private Long totalElements;
	
	private Integer totalPages;
	
	private Boolean lastPage;
}
