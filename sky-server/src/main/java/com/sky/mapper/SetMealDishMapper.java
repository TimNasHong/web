package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    List<Long> getSetMealIds(List<Long> ids);

    void insertIntoSetmealDish(List<SetmealDish> setmealDish);
@Select("select*from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> queryDishById(long id);



@Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> queryBySetmealId(Long id);
}
