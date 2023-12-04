package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    public Result insertDish(@RequestBody DishDTO dishDTO){
        dishService.insert(dishDTO);
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> dishPage(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult=dishService.dishPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    @DeleteMapping()
    public Result deleteDish(@RequestParam List<Long> ids){
        dishService.deleteDish(ids);
        cleanCache("dish_*");
return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> selectDish(@PathVariable Long id){
        DishVO dishVO=dishService.getByIdWithFlavor(id);
return Result.success(dishVO);
    }

    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result statusOrNo(@PathVariable Integer status,long id){
        dishService.statusOrNo(status,id);
        cleanCache("dish_*");
        return Result.success();

    }




    @GetMapping("/list")
    public Result<List<Dish>> listQueryDish(Long categoryId){
        List<Dish>dish=dishService.listQueryDish(categoryId);
        return Result.success(dish);
    }


    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
