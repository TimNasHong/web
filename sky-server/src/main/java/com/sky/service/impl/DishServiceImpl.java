package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
private DishFlavorMapper dishFlavorMapper;
    @Autowired
   private SetMealDishMapper setMealDishMapper;
    @Override
    @Transactional
    public void insert(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        long dishId=dish.getId();

        List<DishFlavor> flavorList=dishDTO.getFlavors();
        if(flavorList!=null&&flavorList.size()>0){
            flavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavorList);
        }
    }

    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        for (Long id : ids) {
            Dish dish=dishMapper.getByIds(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException("起售的套餐不能删除");
            }
        }
        List<Long>setMealIds=setMealDishMapper.getSetMealIds(ids);
        if(setMealIds.size()>0&&setMealIds!=null){
            throw new DeletionNotAllowedException("菜品已关联");
        }
        dishMapper.delete(ids);
        dishFlavorMapper.delete(ids);


    }

    @Override
    public PageResult dishPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.dishPage(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish=dishMapper.getByIds(id);

        List<DishFlavor>list=dishFlavorMapper.getByDishId(id);

        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(list);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        List<DishFlavor>list=dishDTO.getFlavors();
        dishMapper.updateById(dish);

        long dishId=dishDTO.getId();
        List<DishFlavor> flavorList=dishDTO.getFlavors();
        if(flavorList!=null&&flavorList.size()>0){
            flavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavorList);
        }
    }

    @Override
    public void statusOrNo(Integer status, long id) {
       Dish dish=Dish.builder()
               .id(id)
               .status(status)
               .updateTime(LocalDateTime.now())
               .updateUser(BaseContext.getCurrentId())
               .build();
       dishMapper.statusOrNo(dish);

    }

    @Override
    public List<Dish> listQueryDish(long categoryId) {
Dish dish=Dish.builder()
        .categoryId(categoryId)
        .status(StatusConstant.ENABLE)
        .build();
        List<Dish>dishQuery=dishMapper.listQueryDish(dish);

        return dishQuery;
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.listQueryDish(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
