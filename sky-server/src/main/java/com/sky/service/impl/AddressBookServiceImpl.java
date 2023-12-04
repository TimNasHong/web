package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {


    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public void insert(AddressBook addressBook) {
        Long userId= BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> queryList(AddressBook addressBook) {
        return addressBookMapper.queryList(addressBook);
    }

    @Override
    public AddressBook queryById(Long id) {
        return addressBookMapper.queryById(id);
    }

    @Override
    public void setAddress(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.setDefaultAddress(addressBook);

        addressBook.setIsDefault(1);
        addressBookMapper.updateDefaultAddress(addressBook);
    }


    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.updateDefaultAddress(addressBook);
    }
}
