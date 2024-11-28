package com.springboot.ecomproj.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.springboot.ecomproj.dao.UserDao;
import com.springboot.ecomproj.entity.User;

@Component
public class AuthUtil {
	@Autowired
	private UserDao userDao;
	
	public String loggedInEmail() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		User user=userDao.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
		return user.getEmail();
	}
	
	public Long loggedInUserId() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		User user=userDao.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
		return user.getUserId();
	}
	
	public User loggedInUser() {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		User user=userDao.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
		return user;
	}
}
