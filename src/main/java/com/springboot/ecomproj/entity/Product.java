package com.springboot.ecomproj.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
	@Id
	@GeneratedValue (strategy=GenerationType.IDENTITY)
	private Long productId;
	
	@Column (name="pname")
	@NotNull
	private String productName;
	
	@Column (name="image")
	private String image;
	
	@Column (name="pdesc")
	@NotNull
	private String description;
	
	@Column (name="qty")
	@NotNull
	@Min(value = 0, message="Quantity must be at least zero")
	private Integer quantity;
	
	@Column (name="price")
	@NotNull
	@Min(value = 0, message="Price must be at least zero")
	private Double price;
	
	@Column (name="discount")
	private Double discount;
	
	@Column (name="specialPrice")
	private Double specialPrice;
	
	@ManyToOne
	@JoinColumn(name="categoryId")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name="sellerId")
	private User user;
	
	@OneToMany(mappedBy="product", cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true, fetch=FetchType.EAGER)
	private List<CartItem> products;
}