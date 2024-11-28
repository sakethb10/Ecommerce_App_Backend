package com.springboot.ecomproj.entity;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="users", uniqueConstraints= {@UniqueConstraint(columnNames="user_name"), @UniqueConstraint(columnNames="email")})
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	
	@Column(name="user_name")
	@NotBlank
	private String userName;
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String password;
	
	@ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch=FetchType.EAGER)
	@JoinTable(name="user_role", joinColumns=@JoinColumn(name="userId"), inverseJoinColumns=@JoinColumn(name="roleId"))
	private Set<Role> roles;
	
	@OneToMany(mappedBy="user", cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	private List<Address> addresses;
	
	@ToString.Exclude
	@OneToMany(mappedBy="user", cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	private Set<Product> products;
	
	@ToString.Exclude
	@OneToOne(mappedBy="user", cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	private Cart cart;
	
	public User(String username, String email, String password) {
		this.userName=username;
		this.email=email;
		this.password=password;
	}
}
