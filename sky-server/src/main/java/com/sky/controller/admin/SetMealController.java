package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetMealController")
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetMealController {


    @Autowired
    private SetMealService setMealService;

@GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
    PageResult pageResult=setMealService.page(setmealPageQueryDTO);
    return Result.success(pageResult);
}


    @PostMapping
    @CacheEvict(cacheNames = "setmealCache",key="#setmealDTO.categoryId")
    public Result saveSetmeal(@RequestBody SetmealDTO setmealDTO){
setMealService.saveSetmeal(setmealDTO);
    return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> queryById(@PathVariable Long id){

    SetmealVO setmealVO=setMealService.queryById(id);

    return Result.success(setmealVO);
    }


    @PutMapping
//    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateSetmealAndSetmealDish(@RequestBody SetmealDTO setmealDTO){
    setMealService.updateSetmealAndSetmealDish(setmealDTO);
    return Result.success();
    }

    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result statusisOneOrZero(@PathVariable Integer status,long id){
    setMealService.updateSetmealDishStatus(status,id);
    return Result.success();
    }


    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteByIds(@RequestParam List<Long> ids){
    setMealService.deleteByIds(ids);
    return Result.success();
    }
}
