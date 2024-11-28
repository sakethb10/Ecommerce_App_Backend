package com.springboot.ecomproj.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.Address;

@Repository
public interface AddressDao extends JpaRepository<Address, Long>{
	@Query("SELECT a FROM Address a WHERE a.user.userId=:userId")
	public List<Address> findByUserId(Long userId);

}
