package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void insert(AddressBook addressBook);

    List<AddressBook> queryList(AddressBook addressBook);

    AddressBook queryById(Long id);

    void setAddress(AddressBook addressBook);

    void deleteById(Long id);


    void update(AddressBook addressBook);
}
