package com.springboot.ecomproj.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ecomproj.dao.RoleDao;
import com.springboot.ecomproj.dao.UserDao;
import com.springboot.ecomproj.entity.AppRole;
import com.springboot.ecomproj.entity.Role;
import com.springboot.ecomproj.entity.User;
import com.springboot.ecomproj.security.jwt.JwtUtils;
import com.springboot.ecomproj.security.request.LoginRequest;
import com.springboot.ecomproj.security.request.SignupRequest;
import com.springboot.ecomproj.security.request.UpdateRequest;
import com.springboot.ecomproj.security.response.MessageResponse;
import com.springboot.ecomproj.security.response.UserInfoResponse;
import com.springboot.ecomproj.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value="/api/auth")
public class AuthController {
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private RoleDao roleDao;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping(value="/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		Authentication authentication;
		try {
			authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		}catch(AuthenticationException e) {
			Map<String, Object> map=new HashMap<>();
			map.put("message", "Bad Credentials");
			map.put("status", false);
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails=(UserDetailsImpl)authentication.getPrincipal();
		String jwtToken=jwtUtils.generateTokenFromUsername(userDetails);
		List<String> roles=userDetails.getAuthorities().stream().map((item)->item.getAuthority()).toList();
		UserInfoResponse response=new UserInfoResponse(userDetails.getId(), jwtToken, loginRequest.getUsername(), roles);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(value="/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
		if(userDao.existsByUserName(signupRequest.getUsername())) {
			return new ResponseEntity<>(new MessageResponse("Username Is Already Taken!"), HttpStatus.BAD_REQUEST);
		}
		if(userDao.existsByEmail(signupRequest.getEmail())) {
			return new ResponseEntity<>(new MessageResponse("Email Already Exists!"), HttpStatus.BAD_REQUEST);
		}
		User user=new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
		Set<String> strRoles=signupRequest.getRole();
		Set<Role> roles=new HashSet<>();
		Role userRole=roleDao.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
		roles.add(userRole);
		if(strRoles!=null) {
			strRoles.forEach((role)->{
				switch(role) {
				case "admin": 
					Role adminRole=roleDao.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
					roles.add(adminRole);
					break;
				case "seller":
					Role sellerRole=roleDao.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
					roles.add(sellerRole);
					break;
				default:
					Role defaultRole=roleDao.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
					roles.add(defaultRole);
				}
			});
		}
		user.setRoles(roles);
		User savedUser=userDao.save(user);
		UserInfoResponse response=modelMapper.map(savedUser, UserInfoResponse.class);
		List<String> responseRoles=roles.stream().map((role)->role.getRoleName().name()).toList();
		response.setRoles(responseRoles);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value="/username")
	public ResponseEntity<String> currentUsername(Authentication authentication){
		if(authentication!=null) {
			return new ResponseEntity<>(authentication.getName(), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(value="/userDetails")
	public ResponseEntity<?> getUserDetails(Authentication authentication){
		UserDetailsImpl userDetailsImpl=(UserDetailsImpl)authentication.getPrincipal();
		List<String> roles=userDetailsImpl.getAuthorities().stream().map((item)->item.getAuthority()).toList();
		String jwtToken=jwtUtils.generateTokenFromUsername(userDetailsImpl);
		UserInfoResponse response=new UserInfoResponse(userDetailsImpl.getId(), jwtToken, userDetailsImpl.getUsername(), roles);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping(value="/userDetails")
	public ResponseEntity<?> updateUserDetails(@Valid @RequestBody UpdateRequest updateRequest){
		User user=userDao.findByEmail(updateRequest.getEmail());
		if(user==null) {
			return new ResponseEntity<>("Invalid Email!", HttpStatus.BAD_REQUEST);
		}
		user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
		Set<String> requestRoles=updateRequest.getRole();
		Set<Role> roles=new HashSet<>();
		Role userRole=roleDao.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
		roles.add(userRole);
		if(requestRoles!=null) {
			requestRoles.forEach((requestRole)->{
				switch(requestRole) {
				case "admin":
					Role adminRole=roleDao.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
					roles.add(adminRole);
					break;
				case "seller":
					Role sellerRole=roleDao.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
					roles.add(sellerRole);
					break;
				default:
					Role defaultRole=roleDao.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role Is Not Found"));
					roles.add(defaultRole);
				}
			});
		}
		user.setRoles(roles);
		User savedUser=userDao.save(user);
		UserInfoResponse response=modelMapper.map(savedUser, UserInfoResponse.class);
		List<String> responseRoles=roles.stream().map((role)->role.getRoleName().name()).toList();
		response.setRoles(responseRoles);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
