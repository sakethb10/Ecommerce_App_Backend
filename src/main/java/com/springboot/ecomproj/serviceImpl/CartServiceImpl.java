package com.springboot.ecomproj.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ecomproj.dao.CartDao;
import com.springboot.ecomproj.dao.CartItemDao;
import com.springboot.ecomproj.dao.ProductDao;
import com.springboot.ecomproj.dto.CartDto;
import com.springboot.ecomproj.dto.ProductDto;
import com.springboot.ecomproj.entity.Cart;
import com.springboot.ecomproj.entity.CartItem;
import com.springboot.ecomproj.entity.Product;
import com.springboot.ecomproj.exceptions.APIException;
import com.springboot.ecomproj.exceptions.ResourceNotFoundException;
import com.springboot.ecomproj.service.CartService;
import com.springboot.ecomproj.util.AuthUtil;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService{
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CartDao cartDao;
	
	@Autowired
	private CartItemDao cartItemDao;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AuthUtil authUtil;
	
	@Override
	public CartDto addProductToCart(Long productId, Integer quantity) throws ResourceNotFoundException, APIException{
		Cart cart=createCart();
		Product product=productDao.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", productId));
		CartItem cartItem=cartItemDao.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId());
		if(cartItem!=null) {
			//throw new APIException("Product "+product.getProductName()+" Already Exists In The Cart!");
			return updateProductQuantityInCart(productId, quantity);
		}
		if(product.getQuantity()==0) {
			throw new APIException("Product "+product.getProductName()+" Is Not Available!");
		}
		if(product.getQuantity()<quantity) {
			throw new APIException("Not Enough "+product.getProductName()+"s Available!");
		}
		CartItem newCartItem=new CartItem();
		newCartItem.setCart(cart);
		newCartItem.setProduct(product);
		newCartItem.setQuantity(quantity);
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setProductPrice(product.getSpecialPrice());
		cartItemDao.save(newCartItem);
		product.setQuantity(product.getQuantity());
		cart.setTotalPrice(cart.getTotalPrice()+(newCartItem.getProductPrice()*quantity));
		cart.getCartItems().add(newCartItem);
		cartDao.save(cart);
		CartDto response=modelMapper.map(cart, CartDto.class);
		List<CartItem> cartItems=cart.getCartItems();
		List<ProductDto> products=cartItems.stream().map((item)->{
			ProductDto map=modelMapper.map(item.getProduct(), ProductDto.class);
			map.setQuantity(item.getQuantity());
			return map;
		}).toList();
		response.setProducts(products);
		return response;
	}

	private Cart createCart() {
		Cart existing=cartDao.findCartByEmail(authUtil.loggedInEmail());
		if(existing!=null) {
			return existing;
		}
		Cart newCart=new Cart();
		newCart.setTotalPrice(0.0);
		newCart.setUser(authUtil.loggedInUser());
		Cart savedCart=cartDao.save(newCart);
		return savedCart;
	}

	@Override
	public List<CartDto> getAllCarts() throws APIException{
		List<Cart> savedCarts=cartDao.findAll();
		if(savedCarts.size()==0) {
			throw new APIException("No Cart Exists!");
		}
		List<CartDto> response=savedCarts.stream().map((item)->{
			CartDto cartDto=modelMapper.map(item, CartDto.class);
			List<ProductDto> products=item.getCartItems().stream().map((cartItem)->{
				ProductDto productDto=modelMapper.map(cartItem.getProduct(), ProductDto.class);
				productDto.setQuantity(cartItem.getQuantity());
				return productDto;
			}).toList();
			cartDto.setProducts(products);
			return cartDto;
		}).toList();
		return response;
	}

	@Override
	public CartDto getCart(String emailId, Long cartId) throws ResourceNotFoundException{
		Cart cart=cartDao.findCartByEmailAndCartId(emailId, cartId);
		if(cart==null) {
			throw new ResourceNotFoundException("Cart", "Cart Id", cartId);
		}
		CartDto response=modelMapper.map(cart, CartDto.class);
		List<CartItem> cartItems=cart.getCartItems();
		cartItems.forEach((cartItem)->cartItem.getProduct().setQuantity(cartItem.getQuantity()));
		List<ProductDto> products=cartItems.stream().map((item)->{
			Product product=item.getProduct();
			ProductDto productDto=modelMapper.map(product, ProductDto.class);
			return productDto;
		}).toList();
		response.setProducts(products);
		return response;
	}

	@Transactional
	@Override
	public CartDto updateProductQuantityInCart(Long productId, Integer quantity) throws ResourceNotFoundException, APIException{
		String emailId=authUtil.loggedInEmail();
		Cart userCart=cartDao.findCartByEmail(emailId);
		if(userCart==null) {
			throw new ResourceNotFoundException("Cart", "Id", emailId);
		}
		Long cartId=userCart.getCartId();
		Product product=productDao.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", productId));
		if(product.getQuantity()==0&&quantity>0) {
			throw new APIException("Product Is Not Available!");
		}
		if(product.getQuantity()<quantity) {
			throw new APIException("Insufficient Stock!");
		}
		CartItem cartItem=cartItemDao.findCartItemByProductIdAndCartId(cartId, productId);
		if(cartItem==null) {
			throw new ResourceNotFoundException("CartItem", "CartId", cartId);
		}
		Integer newQuantity=cartItem.getQuantity()+quantity;
		if(newQuantity<0) {
			throw new APIException("Resulting Quantity Cannot Be Negative!");
		}
		if(newQuantity==0) {
			deleteProductFromCart(cartId, productId);
		}
		else {
			userCart.setTotalPrice(userCart.getTotalPrice()-(cartItem.getQuantity()*cartItem.getProductPrice()));
			cartItem.setProductPrice(product.getSpecialPrice());
			cartItem.setQuantity(cartItem.getQuantity()+quantity);
			cartItem.setDiscount(product.getDiscount());
			userCart.setTotalPrice(userCart.getTotalPrice()+(cartItem.getQuantity()*cartItem.getProductPrice()));
			cartDao.save(userCart);
		}
		cartItemDao.save(cartItem);
		CartDto response=modelMapper.map(userCart, CartDto.class);
		List<CartItem> cartItems=userCart.getCartItems();
		List<ProductDto> products=cartItems.stream().map((item)->{
			Product prod=item.getProduct();
			ProductDto productDto=modelMapper.map(prod, ProductDto.class);
			productDto.setQuantity(item.getQuantity());
			return productDto;
		}).toList();
		response.setProducts(products);
		return response;
	}

	@Transactional
	@Override
	public String deleteProductFromCart(Long cartId, Long productId) throws ResourceNotFoundException{
		Cart userCart=cartDao.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart", "CartId", cartId));
		CartItem cartItem=cartItemDao.findCartItemByProductIdAndCartId(cartId, productId);
		if(cartItem==null) {
			throw new ResourceNotFoundException("Product", "ProductId", productId);
		}
		userCart.setTotalPrice(userCart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
		cartItemDao.deleteCartItemByProductIdAndCartId(cartId, productId);
		cartDao.save(userCart);
		return "Product "+ cartItem.getProduct().getProductName() + " Removed From Cart!";
	}

	@Transactional 
	@Override
	public void updateProductInCarts(Long cartId, Long productId) throws ResourceNotFoundException, APIException{
		Cart cart=cartDao.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart", "CartId", cartId));
		Product product=productDao.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", productId));
		CartItem cartItem=cartItemDao.findCartItemByProductIdAndCartId(cartId, productId);
		if(cartItem==null) {
			throw new APIException("Product "+product.getProductName()+" Not Found!");
		}
		Double cartPrice=cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
		cartItem.setProductPrice(product.getSpecialPrice());
		cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));
		cartItem=cartItemDao.save(cartItem);
	}

	@Transactional
	@Override
	public String clearCart(Long cartId) throws ResourceNotFoundException, APIException {
	    Cart cart=cartDao.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart", "CartId", cartId));
	    List<CartItem> cartItems=new ArrayList<>(cart.getCartItems());
	    if (cartItems.isEmpty()){
	        throw new APIException("Cart Is Already Empty!");
	    }
	    cartItemDao.deleteAllInBatch(cartItems);
	    cart.setTotalPrice(0.0);
	    cartDao.save(cart);
	    return "Cart Cleared!";
	}

}
