package com.springboot.ecomproj.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springboot.ecomproj.dto.ProductDto;
import com.springboot.ecomproj.entity.Product;

@Configuration
public class AppConfig {
    @Bean
    ModelMapper modelMapper() {
    	ModelMapper modelMapper=new ModelMapper();
    	
    	// Custom mapping for sellerUsername
        modelMapper.typeMap(Product.class, ProductDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getUserName(), ProductDto::setSellerUsername);
        });
        
		return modelMapper;
	}
}
