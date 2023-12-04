package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void insert(DishDTO dishDTO);

    PageResult dishPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);


    DishVO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    void statusOrNo(Integer status, long id);

    List<Dish> listQueryDish(long categoryId);

    List<DishVO> listWithFlavor(Dish dish);
}
