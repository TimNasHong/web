package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    PageResult categoryPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void statusOrStop(Integer status,long id);

    void insert(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void deleteById(long id);


    List<Category> list(Integer type);
}
