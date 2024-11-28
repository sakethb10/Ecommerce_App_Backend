package com.springboot.ecomproj.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.Order;

@Repository
public interface OrderDao extends JpaRepository<Order, Long>{
	@Query("SELECT o FROM Order o WHERE o.email=:emailId")
	List<Order> findByEmail(String emailId);

}
