package com.news.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.news.sports.entity.Address;
import com.news.sports.mapper.AddressMapper;
import com.news.sports.service.AddressService;
import com.news.sports.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;
    private final UserService userService;

    public AddressServiceImpl(AddressMapper addressMapper, UserService userService) {
        this.addressMapper = addressMapper;
        this.userService = userService;
    }

    @Override
    public List<Address> getAddressList() {
        // 获取当前用户ID
        Long userId = userService.getCurrentUser().getId();
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
    }

    @Override
    @Transactional
    public Address addAddress(Address address) {
        // 设置用户ID
        address.setUserId(userService.getCurrentUser().getId());
        
        // 如果是默认地址，将其他地址设为非默认
        if (address.getIsDefault() == 1) {
            clearDefaultAddress(address.getUserId());
        }
        
        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public void updateAddress(Address address) {
        // 验证地址所属
        Address existingAddress = addressMapper.selectById(address.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userService.getCurrentUser().getId())) {
            throw new RuntimeException("无权限修改此地址");
        }
        
        // 如果是默认地址，将其他地址设为非默认
        if (address.getIsDefault() == 1) {
            clearDefaultAddress(address.getUserId());
        }
        
        addressMapper.updateById(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        // 验证地址所属
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userService.getCurrentUser().getId())) {
            throw new RuntimeException("无权限删除此地址");
        }
        
        addressMapper.deleteById(id);
    }

    private void clearDefaultAddress(Long userId) {
        Address updateAddress = new Address();
        updateAddress.setIsDefault(0);
        addressMapper.update(updateAddress, new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));
    }
} 