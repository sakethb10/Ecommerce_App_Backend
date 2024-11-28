package com.springboot.ecomproj.service;

import java.util.List;

import com.springboot.ecomproj.dto.AddressDto;
import com.springboot.ecomproj.entity.User;

import jakarta.validation.Valid;

public interface AddressService {

	public AddressDto createAddress(AddressDto addressDto, User user);

	public List<AddressDto> getAllAddresses(User user);

	public AddressDto getAddressById(Long addressId);

	public AddressDto updateAddress(@Valid AddressDto addressDto, Long addressId);

	public AddressDto deleteAddress(Long addressId);

}
