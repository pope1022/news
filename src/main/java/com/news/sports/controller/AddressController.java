package com.news.sports.controller;

import com.news.sports.common.Result;
import com.news.sports.entity.Address;
import com.news.sports.service.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public Result<List<Address>> getAddressList() {
        return Result.success(addressService.getAddressList());
    }

    @PostMapping
    public Result<Address> addAddress(@RequestBody Address address) {
        return Result.success(addressService.addAddress(address));
    }

    @PutMapping
    public Result<Void> updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return Result.success();
    }
} 