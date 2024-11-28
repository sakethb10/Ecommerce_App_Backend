package com.springboot.ecomproj.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Long>{
	Optional<User> findByUserName(String username);

	boolean existsByUserName(String username);

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.email=:userEmail")
	User findByEmail(String userEmail);
}
