package com.springboot.ecomproj.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.ecomproj.entity.AppRole;
import com.springboot.ecomproj.entity.Role;

@Repository
public interface RoleDao extends JpaRepository<Role, Long>{

	Optional<Role> findByRoleName(AppRole roleUser);

}
