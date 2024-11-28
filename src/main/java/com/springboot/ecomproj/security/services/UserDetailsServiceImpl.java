package com.springboot.ecomproj.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.springboot.ecomproj.dao.UserDao;
import com.springboot.ecomproj.entity.User;

@Component
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userDao.findByUserName(username).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
		return UserDetailsImpl.build(user);
	}
	
}
