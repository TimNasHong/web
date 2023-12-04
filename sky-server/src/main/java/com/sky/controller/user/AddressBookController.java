package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user/addressBook")
public class AddressBookController {


    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    public Result insertAddress(@RequestBody AddressBook addressBook){
        addressBookService.insert(addressBook);
        return Result.success();
    }


    @GetMapping("/list")
    public Result<List<AddressBook>> list(){
        AddressBook addressBook=new AddressBook();
        Long userID= BaseContext.getCurrentId();
        addressBook.setUserId(userID);
        List<AddressBook>list=addressBookService.queryList(addressBook);
        return Result.success(list);
    }



    @GetMapping("/{id}")
    public Result<AddressBook> queryById(@RequestParam Long id){
        AddressBook addressBook=addressBookService.queryById(id);
        return Result.success(addressBook);
    }


    @PutMapping("/default")
    public Result setAddress(@RequestBody AddressBook addressBook){
        addressBookService.setAddress(addressBook);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result deleteAdddress(@RequestParam Long id){
        addressBookService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/default")
    public Result<AddressBook> queryDefaultAddress(){
        Long userID=BaseContext.getCurrentId();
       AddressBook addressBook=new AddressBook();
       addressBook.setIsDefault(1);
       addressBook.setUserId(userID);
       List<AddressBook>list=addressBookService.queryList(addressBook);

       if(list!=null&&list.size()==1){
           return Result.success(list.get(0));
       }
       return Result.error("没有找到默认地址!");
    }

    @PutMapping
    public Result updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.update(addressBook);
        return Result.success();
    }
}
