package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {
    @Select("select count(id) from setmeal where category_id=#{id}")
    Integer countByCategoryId(long id);



    Page<SetmealVO> pageInert(SetmealPageQueryDTO setmealPageQueryDTO);
@AutoFill(value = OperationType.INSERT)
    void insertIntoSetmeal(Setmeal setmeal);
@Select("select category_id,name,price,status,description,image,update_time from setmeal where id=#{id}")
    Setmeal queryById(Long id);
@AutoFill(value = OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

@AutoFill(value = OperationType.UPDATE)
    void updaetSetmealDishStatus(Setmeal setmeal);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
@Delete("delete from setmeal where id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
