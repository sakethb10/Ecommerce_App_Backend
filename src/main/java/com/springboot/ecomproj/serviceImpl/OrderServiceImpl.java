package com.springboot.ecomproj.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ecomproj.dao.AddressDao;
import com.springboot.ecomproj.dao.CartDao;
import com.springboot.ecomproj.dao.OrderDao;
import com.springboot.ecomproj.dao.OrderItemDao;
import com.springboot.ecomproj.dao.PaymentDao;
import com.springboot.ecomproj.dao.ProductDao;
import com.springboot.ecomproj.dto.OrderDto;
import com.springboot.ecomproj.dto.OrderItemDto;
import com.springboot.ecomproj.dto.ProductDto;
import com.springboot.ecomproj.entity.Address;
import com.springboot.ecomproj.entity.Cart;
import com.springboot.ecomproj.entity.CartItem;
import com.springboot.ecomproj.entity.Order;
import com.springboot.ecomproj.entity.OrderItem;
import com.springboot.ecomproj.entity.Payment;
import com.springboot.ecomproj.entity.Product;
import com.springboot.ecomproj.exceptions.APIException;
import com.springboot.ecomproj.exceptions.ResourceNotFoundException;
import com.springboot.ecomproj.service.CartService;
import com.springboot.ecomproj.service.OrderService;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService{
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private CartDao cartDao;
	
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private PaymentDao paymentDao;
	
	@Autowired
	private OrderItemDao orderItemDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CartService cartService;
	
	@Override
	@Transactional
	public OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
			String pgStatus, String pgResponseMessage) throws ResourceNotFoundException, APIException{
		Cart userCart=cartDao.findCartByEmail(emailId);
		if(userCart==null||userCart.getCartItems().isEmpty()) {
			throw new APIException("Cart Is Empty Or Doesn't Exist!	");
		}
		Address address=addressDao.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address", "AddressId", addressId));
		Order order=new Order();
		order.setEmail(emailId);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(userCart.getTotalPrice());
		order.setOrderStatus("Order Accepted");
		order.setAddress(address);
		Payment payment=new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
		payment.setOrder(order);
		Payment savedPayment=paymentDao.save(payment);
		order.setPayment(savedPayment);
		List<CartItem> cartItems=userCart.getCartItems();
		if(cartItems.isEmpty()) {
			throw new APIException("There Are No Items In The Cart!");
		}
		List<OrderItem> orderItems=cartItems.stream().map((cartItem)->{
			Integer cartQuantity=cartItem.getQuantity();
			Product product=cartItem.getProduct();
			Product savedProduct=productDao.findById(product.getProductId()).orElseThrow(()->new ResourceNotFoundException("Product", "ProductId", product.getProductId()));
			if(savedProduct.getQuantity()<cartQuantity) {
				throw new APIException("Not Enough Stock To Place Order!");
			}
			product.setQuantity(product.getQuantity()-cartQuantity);
			productDao.save(product);
			OrderItem orderItem=new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(order);
			return orderItem;
		}).collect(Collectors.toList());
		orderItemDao.saveAll(orderItems);
		cartService.clearCart(userCart.getCartId());
		order.setOrderItems(orderItems);
		Order savedOrder=orderDao.save(order);
		OrderDto response=new OrderDto();
		response.setOrderId(savedOrder.getOrderId());
		response.setEmail(savedOrder.getEmail());
		response.setOrderDate(savedOrder.getOrderDate());
		response.setTotalAmount(savedOrder.getTotalAmount());
		response.setOrderStatus(savedOrder.getOrderStatus());
		response.setAddressId(addressId);
		List<OrderItemDto> responseList=savedOrder.getOrderItems().stream().map((orderItem)->{
			OrderItemDto orderItemDto=new OrderItemDto();
			orderItemDto.setOrderItemId(orderItem.getOrderItemId());
			Product prod=orderItem.getProduct();
			ProductDto prodDto=new ProductDto();
			prodDto.setProductId(prod.getProductId());
			prodDto.setProductName(prod.getProductName());
			prodDto.setImage(prod.getImage());
			prodDto.setDescription(prod.getDescription());
			prodDto.setQuantity(prod.getQuantity());
			prodDto.setPrice(prod.getPrice());
			prodDto.setDiscount(prod.getDiscount());
			prodDto.setSpecialPrice(prod.getSpecialPrice());
			prodDto.setSellerUsername(prod.getUser().getUserName());
			orderItemDto.setProduct(prodDto);
			orderItemDto.setOrderId(orderItem.getOrder().getOrderId());
			orderItemDto.setQuantity(orderItem.getQuantity());
			orderItemDto.setDiscount(orderItem.getDiscount());
			orderItemDto.setOrderedProductPrice(orderItem.getOrderedProductPrice());
			return orderItemDto;
		}).toList();
		response.setOrderItems(responseList);
		return response;
	}

	@Transactional
	@Override
	public List<OrderDto> getAllProducts() throws APIException{
		List<Order> savedOrders=orderDao.findAll();
		if(savedOrders==null||savedOrders.isEmpty()) {
			throw new APIException("There Are No Orders!");
		}
		List<OrderDto> response=savedOrders.stream().map((order)->{
			OrderDto orderDto=new OrderDto();
			orderDto.setOrderId(order.getOrderId());
			orderDto.setEmail(order.getEmail());
			orderDto.setOrderDate(order.getOrderDate());
			orderDto.setTotalAmount(order.getTotalAmount());
			orderDto.setOrderStatus(order.getOrderStatus());
			orderDto.setAddressId(order.getAddress().getAddressId());
			List<OrderItemDto> responseList=order.getOrderItems().stream().map((orderItem)->{
				OrderItemDto orderItemDto=new OrderItemDto();
				orderItemDto.setOrderItemId(orderItem.getOrderItemId());
				Product prod=orderItem.getProduct();
				ProductDto prodDto=new ProductDto();
				prodDto.setProductId(prod.getProductId());
				prodDto.setProductName(prod.getProductName());
				prodDto.setImage(prod.getImage());
				prodDto.setDescription(prod.getDescription());
				prodDto.setQuantity(prod.getQuantity());
				prodDto.setPrice(prod.getPrice());
				prodDto.setDiscount(prod.getDiscount());
				prodDto.setSpecialPrice(prod.getSpecialPrice());
				prodDto.setSellerUsername(prod.getUser().getUserName());
				orderItemDto.setProduct(prodDto);
				orderItemDto.setOrderId(orderItem.getOrder().getOrderId());
				orderItemDto.setQuantity(orderItem.getQuantity());
				orderItemDto.setDiscount(orderItem.getDiscount());
				orderItemDto.setOrderedProductPrice(orderItem.getOrderedProductPrice());
				return orderItemDto;
			}).toList();
			orderDto.setOrderItems(responseList);
			return orderDto;
		}).toList();
		return response;
	}

	@Override
	public List<OrderDto> getProductsByUserEmail(String emailId) throws APIException{
		List<Order> userOrders=orderDao.findByEmail(emailId);
		if(userOrders==null||userOrders.isEmpty()) {
			throw new APIException("No Orders Found For User!");
		}
		List<OrderDto> response=userOrders.stream().map((order)->{
			OrderDto orderDto=new OrderDto();
			orderDto.setOrderId(order.getOrderId());
			orderDto.setEmail(order.getEmail());
			orderDto.setOrderDate(order.getOrderDate());
			orderDto.setTotalAmount(order.getTotalAmount());
			orderDto.setOrderStatus(order.getOrderStatus());
			orderDto.setAddressId(order.getAddress().getAddressId());
			List<OrderItemDto> responseList=order.getOrderItems().stream().map((orderItem)->{
				OrderItemDto orderItemDto=new OrderItemDto();
				orderItemDto.setOrderItemId(orderItem.getOrderItemId());
				Product prod=orderItem.getProduct();
				ProductDto prodDto=new ProductDto();
				prodDto.setProductId(prod.getProductId());
				prodDto.setProductName(prod.getProductName());
				prodDto.setImage(prod.getImage());
				prodDto.setDescription(prod.getDescription());
				prodDto.setQuantity(prod.getQuantity());
				prodDto.setPrice(prod.getPrice());
				prodDto.setDiscount(prod.getDiscount());
				prodDto.setSpecialPrice(prod.getSpecialPrice());
				prodDto.setSellerUsername(prod.getUser().getUserName());
				orderItemDto.setProduct(prodDto);
				orderItemDto.setOrderId(orderItem.getOrder().getOrderId());
				orderItemDto.setQuantity(orderItem.getQuantity());
				orderItemDto.setDiscount(orderItem.getDiscount());
				orderItemDto.setOrderedProductPrice(orderItem.getOrderedProductPrice());
				return orderItemDto;
			}).toList();
			orderDto.setOrderItems(responseList);
			return orderDto;
		}).toList();
		return response;
	}

}
