package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("admin/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY="SHOP_STATUS";


   @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status){
       redisTemplate.opsForValue().set(KEY,status);
       return Result.success();
   }


   @GetMapping("/status")
    public Result<Integer> getShopStatus(){
       Integer status = (Integer) redisTemplate.opsForValue().get(KEY);

       return Result.success(status);
   }

}
