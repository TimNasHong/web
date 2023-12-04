package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.xmlbeans.impl.schema.StscChecker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> p=setMealMapper.pageInert(setmealPageQueryDTO);

        return new PageResult(p.getTotal(),p.getResult());
    }


    @Override
    @Transactional
    public void saveSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.insertIntoSetmeal(setmeal);


        long setmealId=setmeal.getId();
        List<SetmealDish>setmealDish=setmealDTO.getSetmealDishes();
        setmealDish.forEach(setmealDish1 -> {
            setmealDish1.setSetmealId(setmealId);
        });


        setMealDishMapper.insertIntoSetmealDish(setmealDish);


    }


    @Override
    @Transactional
    public SetmealVO queryById(Long id) {
        Setmeal setmeal=setMealMapper.queryById(id);
        List<SetmealDish>setmealDishes=setMealDishMapper.queryBySetmealId(id);

        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setId(id);


        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }


    @Override
    @Transactional
    public void updateSetmealAndSetmealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.updateSetmeal(setmeal);
        Long setmealId=setmealDTO.getId();
        setMealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish>setmealDish=setmealDTO.getSetmealDishes();
        setmealDish.forEach(setmealDish1 -> {
            setmealDish1.setSetmealId(setmealId);
        });
        setMealDishMapper.insertIntoSetmealDish(setmealDish);


    }

    @Override
    public void updateSetmealDishStatus(Integer status, long id) {
        if(status== StatusConstant.ENABLE){
            List<Dish> dish=dishMapper.selectBySetmealIdFromDish(id);
            if(dish.size()>0&&dish!=null){
               dish.forEach(dishStatus->{
                   if(dishStatus.getStatus()==StatusConstant.DISABLE){
                       throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                   }
               });
            }
        }




        Setmeal setmeal=Setmeal.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        setMealMapper.updaetSetmealDishStatus(setmeal);

    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal=setMealMapper.queryById(id);
            if(setmeal.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

        }
        ids.forEach(setmealId->{
            setMealMapper.deleteBySetmealId(setmealId);
            setMealDishMapper.deleteBySetmealId(setmealId);
        });
    }



    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setMealMapper.list(setmeal);
        return list;
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }




}
