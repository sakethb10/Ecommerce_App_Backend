package com.springboot.ecomproj.serviceImpl;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.springboot.ecomproj.dao.CategoryDao;
import com.springboot.ecomproj.dto.CategoryDto;
import com.springboot.ecomproj.entity.Category;
import com.springboot.ecomproj.exceptions.APIException;
import com.springboot.ecomproj.exceptions.ResourceNotFoundException;
import com.springboot.ecomproj.payload.CategoryResponse;
import com.springboot.ecomproj.service.CategoryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public CategoryDto createCategory(CategoryDto dto) throws APIException{
		Category category=modelMapper.map(dto, Category.class);
		Category savedCategory=categoryDao.findByCategoryName(category.getCategoryName());
		if(savedCategory!=null) {
			throw new APIException("Category "+savedCategory.getCategoryName()+ " Already Exists");
		}
		Category newCategory=categoryDao.save(category);
		return modelMapper.map(newCategory, CategoryDto.class);
	}

	@Override
	public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) throws APIException{
		Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Category> categoryPage=categoryDao.findAll(pageDetails);
		List<Category> list=categoryPage.getContent();
		if(list.isEmpty()) {
			throw new APIException("No Category Exists");
		}
		List<CategoryDto> convertedList=list.stream().map((category)->modelMapper.map(category, CategoryDto.class)).toList();
		CategoryResponse categoryResponse=new CategoryResponse();
		categoryResponse.setContent(convertedList);
		categoryResponse.setPageNumber(categoryPage.getNumber());
		categoryResponse.setPageSize(categoryPage.getSize());
		categoryResponse.setTotalElements(categoryPage.getTotalElements());
		categoryResponse.setTotalPages(categoryPage.getTotalPages());
		categoryResponse.setLastPage(categoryPage.isLast());
		return categoryResponse;
	}

	@Override
	public CategoryDto updateCategory(String categoryName, CategoryDto updatedCategory) throws ResourceNotFoundException{
		Category category=categoryDao.findByCategoryName(categoryName);
		if(category==null) {
			throw new ResourceNotFoundException("Category", "Name", categoryName);
		}
		category.setCategoryName(updatedCategory.getCategoryName());
		Category updated=categoryDao.save(category);
		return modelMapper.map(updated, CategoryDto.class);
	}

	@Override
	public CategoryDto deleteCategory(String categoryName) throws ResourceNotFoundException{
		Category category=categoryDao.findByCategoryName(categoryName);
		if(category==null) {
			throw new ResourceNotFoundException("Category", "Name", categoryName);
		}
		categoryDao.delete(category);
		return modelMapper.map(category, CategoryDto.class);
	}

}
