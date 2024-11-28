package com.springboot.ecomproj.serviceImpl;

import java.io.IOException;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.ecomproj.dao.CartDao;
import com.springboot.ecomproj.dao.CategoryDao;
import com.springboot.ecomproj.dao.ProductDao;
import com.springboot.ecomproj.dao.UserDao;
import com.springboot.ecomproj.dto.CartDto;
import com.springboot.ecomproj.dto.CategoryDto;
import com.springboot.ecomproj.dto.ProductDto;
import com.springboot.ecomproj.entity.Cart;
import com.springboot.ecomproj.entity.Category;
import com.springboot.ecomproj.entity.Product;
import com.springboot.ecomproj.entity.User;
import com.springboot.ecomproj.exceptions.APIException;
import com.springboot.ecomproj.exceptions.ResourceNotFoundException;
import com.springboot.ecomproj.payload.ProductResponse;
import com.springboot.ecomproj.service.CartService;
import com.springboot.ecomproj.service.FileService;
import com.springboot.ecomproj.service.ProductService;

import jakarta.validation.Valid;

@Service
public class ProductServiceImpl implements ProductService{
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CartDao cartDao;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CartService cartService;
	
	@Value ("${project.image}")
	private String path;
	
	@Override
	public ProductDto addProduct(ProductDto productDto, String categoryName, String sellerUsername) throws ResourceNotFoundException{
		Category category=categoryDao.findByCategoryName(categoryName);
		if(category==null) {
			throw new ResourceNotFoundException("Category", "Name", categoryName);
		}
		User user=userDao.findByUserName(sellerUsername).orElseThrow(()->new ResourceNotFoundException("User", "SellerUsername", sellerUsername));
		Product product=modelMapper.map(productDto, Product.class);
		product.setImage("default.png");
		product.setCategory(category);
		product.setUser(user);
		Double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
		product.setSpecialPrice(specialPrice);
		Product savedProduct=productDao.save(product);
		return modelMapper.map(savedProduct, ProductDto.class);
	}

	@Override
	public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) throws APIException{
		Sort sortByAndOrder=sortBy.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productPage=productDao.findAll(pageDetails);
		List<Product> list=productPage.getContent();
		if(list.isEmpty()) {
			throw new APIException("No Product Exists!");
		}
		List<ProductDto> convertedList=list.stream().map((product)->modelMapper.map(product, ProductDto.class)).toList();
		ProductResponse response=new ProductResponse();
		response.setContent(convertedList);
		response.setPageNumber(pageNumber);
		response.setPageSize(pageSize);
		response.setTotalElements(productPage.getTotalElements());
		response.setTotalPages(productPage.getTotalPages());
		response.setLastPage(productPage.isLast());
		return response;
	}

	@Override
	public ProductResponse getProductsByCategory(@Valid CategoryDto categoryDto, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder=sortBy.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productPage=productDao.getProductsByCategoryName(categoryDto.getCategoryName(), pageDetails);
		List<Product> list=productPage.getContent();
		if(list.isEmpty()) {
			throw new APIException("No Products Found For This Category!");
		}
		List<ProductDto> convertedList=list.stream().map((product)->modelMapper.map(product, ProductDto.class)).toList();
		ProductResponse response=new ProductResponse();
		response.setContent(convertedList);
		response.setPageNumber(pageNumber);
		response.setPageSize(pageSize);
		response.setTotalElements(productPage.getTotalElements());
		response.setTotalPages(productPage.getTotalPages());
		response.setLastPage(productPage.isLast());
		return response;
	}

	@Override
	public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder=sortBy.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		Pageable pageDetails=PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productPage=productDao.getProductsByKeyword(keyword, pageDetails);
		List<Product> list=productPage.getContent();
		if(list.isEmpty()) {
			throw new APIException("No Products Match Your Search!");
		}
		List<ProductDto> convertedList=list.stream().map((product)->modelMapper.map(product, ProductDto.class)).toList();
		ProductResponse response=new ProductResponse();
		response.setContent(convertedList);
		response.setPageNumber(pageNumber);
		response.setPageSize(pageSize);
		response.setTotalElements(productPage.getTotalElements());
		response.setTotalPages(productPage.getTotalPages());
		response.setLastPage(productPage.isLast());
		return response;
	}

	@Override
	public ProductDto updateProduct(Long productId, ProductDto productDto) throws ResourceNotFoundException{
		Product savedProduct=productDao.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", productId));
		savedProduct.setProductName(productDto.getProductName());
		savedProduct.setDescription(productDto.getDescription());
		savedProduct.setQuantity(productDto.getQuantity());
		savedProduct.setPrice(productDto.getPrice());
		savedProduct.setDiscount(productDto.getDiscount());
		Double specialPrice=productDto.getPrice()-((productDto.getDiscount()*0.01)*productDto.getPrice());
		savedProduct.setSpecialPrice(specialPrice);
		Product updated=productDao.save(savedProduct);
		List<Cart> carts=cartDao.findCartsByProductId(productId);
		List<CartDto> cartDtos=carts.stream().map((cart)->{
			CartDto cartDto=modelMapper.map(cart, CartDto.class);
			List<ProductDto> products=cart.getCartItems().stream().map((item)->modelMapper.map(item.getProduct(), ProductDto.class)).toList();
			cartDto.setProducts(products);
			return cartDto;
		}).toList();
		cartDtos.forEach((cart)->cartService.updateProductInCarts(cart.getCartId(), productId));
		return modelMapper.map(updated, ProductDto.class);
	}

	@Override
	public ProductDto deleteProduct(Long productId) throws ResourceNotFoundException{
		Product savedProduct=productDao.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", productId));
		List<Cart> carts=cartDao.findCartsByProductId(productId);
		carts.forEach((cart)->cartService.deleteProductFromCart(cart.getCartId(), productId));
		productDao.delete(savedProduct);
		return modelMapper.map(savedProduct, ProductDto.class);
	}

	@Override
	public ProductDto updateProductImage(Long productId, MultipartFile image) throws ResourceNotFoundException, IOException{
		Product savedProduct=productDao.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", productId));
		String fileName=fileService.uploadImage(path, image);
		savedProduct.setImage(fileName);
		Product updatedProduct=productDao.save(savedProduct);
		return modelMapper.map(updatedProduct, ProductDto.class);
	}
}
