package com.springboot.ecomproj.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ecomproj.dto.AddressDto;
import com.springboot.ecomproj.entity.User;
import com.springboot.ecomproj.service.AddressService;
import com.springboot.ecomproj.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value="/api")
public class AddressController {
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private AuthUtil authUtil;
	
	@PostMapping(value="/addresses")
	public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDto){
		User user=authUtil.loggedInUser();
		AddressDto response=addressService.createAddress(addressDto, user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value="/users/addresses")
	public ResponseEntity<List<AddressDto>> getAddresses(){
		User user=authUtil.loggedInUser();
		List<AddressDto> response=addressService.getAllAddresses(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value="/addresses/{addressId}")
	public ResponseEntity<AddressDto> getAddressById(@PathVariable Long addressId){
		AddressDto response=addressService.getAddressById(addressId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping(value="/addresses/{addressId}")
	public ResponseEntity<AddressDto> updateAddress(@Valid @RequestBody AddressDto addressDto, @PathVariable Long addressId){
		AddressDto response=addressService.updateAddress(addressDto, addressId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping(value="/addresses/{addressId}")
	public ResponseEntity<AddressDto> deleteAddress(@PathVariable Long addressId){
		AddressDto response=addressService.deleteAddress(addressId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
