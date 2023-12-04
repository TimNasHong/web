package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

@Select("select count(id) from dish where category_id=#{id}")
    Integer countByCategoryId(long id);
@AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);


    Page<DishVO> dishPage(DishPageQueryDTO dishPageQueryDTO);
@Select("select *from dish where id=#{id}")
    Dish getByIds(Long id);

    void delete(List<Long> ids);
@Select("select*from dish")
    DishVO getByIdWithFlavor(Long id);
@AutoFill(value = OperationType.UPDATE)
    void updateById(Dish dish);

    void statusOrNo(Dish dish);


    List<Dish> listQueryDish(Dish dish);
@Select("select d.* from dish d left outer join setmeal_dish sd on d.id = sd.dish_id where sd.setmeal_id=#{id}")
    List<Dish> selectBySetmealIdFromDish(long id);
}
