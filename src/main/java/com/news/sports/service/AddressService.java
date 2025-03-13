package com.news.sports.service;

import com.news.sports.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> getAddressList();
    
    Address addAddress(Address address);
    
    void updateAddress(Address address);
    
    void deleteAddress(Long id);
} 