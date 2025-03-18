package com.pjb.kindergarten_suggestion.services;

import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.entities.Address;
import com.pjb.kindergarten_suggestion.repositories.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Address handleSaveAddress(Address address) {
        return this.addressRepository.save(address);
    }

}
