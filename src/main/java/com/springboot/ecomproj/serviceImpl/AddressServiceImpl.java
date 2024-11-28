package com.springboot.ecomproj.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ecomproj.dao.AddressDao;
import com.springboot.ecomproj.dao.UserDao;
import com.springboot.ecomproj.dto.AddressDto;
import com.springboot.ecomproj.entity.Address;
import com.springboot.ecomproj.entity.User;
import com.springboot.ecomproj.exceptions.APIException;
import com.springboot.ecomproj.exceptions.ResourceNotFoundException;
import com.springboot.ecomproj.service.AddressService;

import jakarta.validation.Valid;

@Service
public class AddressServiceImpl implements AddressService{
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public AddressDto createAddress(AddressDto addressDto, User user) {
		Address address=modelMapper.map(addressDto, Address.class);
		List<Address> addresses=user.getAddresses();
		addresses.add(address);
		user.setAddresses(addresses);
		address.setUser(user);
		userDao.save(user);
		return modelMapper.map(address, AddressDto.class);
	}

	@Override
	public List<AddressDto> getAllAddresses(User user) throws APIException{
		Long userId=user.getUserId();
		List<Address> savedAddresses=addressDao.findByUserId(userId);
		if(savedAddresses==null) {
			throw new APIException("User "+user.getUserName()+" Has No Registered Addresses!");
		}
		List<AddressDto> response=savedAddresses.stream().map((address)->modelMapper.map(address, AddressDto.class)).toList();
		return response;
	}

	@Override
	public AddressDto getAddressById(Long addressId) throws ResourceNotFoundException{
		Address savedAddress=addressDao.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address", "AddressId", addressId));
		return modelMapper.map(savedAddress, AddressDto.class);
	}

	@Override
	public AddressDto updateAddress(@Valid AddressDto addressDto, Long addressId) throws ResourceNotFoundException{
		Address savedAddress=addressDao.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address", "AddressId", addressId));
		Address newAddress=modelMapper.map(addressDto, Address.class);
		savedAddress=newAddress;
		Address updatedAddress=addressDao.save(savedAddress);
		User user=savedAddress.getUser();
		user.getAddresses().removeIf((address)->address.getAddressId().equals(addressId));
		user.getAddresses().add(updatedAddress);
		userDao.save(user);
		return modelMapper.map(updatedAddress, AddressDto.class);
	}

	@Override
	public AddressDto deleteAddress(Long addressId) throws ResourceNotFoundException{
		Address savedAddress=addressDao.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address", "AddressId", addressId));
		User user=savedAddress.getUser();
		user.getAddresses().removeIf((address)->address.getAddressId().equals(addressId));
		userDao.save(user);
		addressDao.delete(savedAddress);
		return modelMapper.map(savedAddress, AddressDto.class);
	}

}
