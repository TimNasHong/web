package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userShoppingCart")
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public Result addIntoShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }



    @GetMapping("/list")
    public Result<List<ShoppingCart>> queryShoppingCart(){
        List<ShoppingCart> list=shoppingCartService.list();
        return Result.success(list);
    }


    @PostMapping("/sub")
    public Result deleteOneOfShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }


    @DeleteMapping("/clean")
    public Result deleteAllShoppingCart(){
        shoppingCartService.clean();
        return Result.success();
    }
}
