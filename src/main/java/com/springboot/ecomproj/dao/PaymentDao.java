package com.springboot.ecomproj.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.Payment;

@Repository
public interface PaymentDao extends JpaRepository<Payment, Long>{

}
