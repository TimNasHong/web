package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

@GetMapping("/page")
    public Result<PageResult> categoryPage(CategoryPageQueryDTO categoryPageQueryDTO){
    PageResult pageResult=categoryService.categoryPage(categoryPageQueryDTO);
    return Result.success(pageResult);
}
@PostMapping("/status/{status}")
    public Result statusOrStop(@PathVariable Integer status,long id){
    categoryService.statusOrStop(status,id);
    return Result.success();
}
@PostMapping
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO){
    categoryService.insert(categoryDTO);
    return Result.success();
}

@PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
    categoryService.update(categoryDTO);
    return Result.success();
}

@DeleteMapping
    public Result delete(long id ){
    categoryService.deleteById(id);
    return Result.success();
}
@GetMapping("/list")
    public Result<List<Category>> list(Integer type){
    List<Category>list=categoryService.list(type);
    return Result.success(list);
}
}
