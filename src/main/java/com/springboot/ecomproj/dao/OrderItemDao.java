package com.springboot.ecomproj.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.OrderItem;

@Repository
public interface OrderItemDao extends JpaRepository<OrderItem, Long>{

}
