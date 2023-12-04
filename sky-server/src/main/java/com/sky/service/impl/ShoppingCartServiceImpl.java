package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list=shoppingCartMapper.queryLiveOrNot(shoppingCart);


        if(list!=null&&list.size()>0){
            ShoppingCart cart=list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateShoppingCartByUserId(cart);
        }else {
             Long dishId=shoppingCartDTO.getDishId();
             if(dishId!=null){
                 Dish dish=dishMapper.getByIds(dishId);
                 shoppingCart.setAmount(dish.getPrice());
                 shoppingCart.setName(dish.getName());
                 shoppingCart.setImage(dish.getImage());

             }else {
                 Long setmealId=shoppingCartDTO.getSetmealId();
                 Setmeal setmeal=setMealMapper.queryById(setmealId);
                 shoppingCart.setAmount(setmeal.getPrice());
                 shoppingCart.setName(setmeal.getName());
                 shoppingCart.setImage(setmeal.getImage());

             }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);
        }
    }


    @Override
    public List<ShoppingCart> list() {
        Long userId=BaseContext.getCurrentId();
        ShoppingCart shoppingCart=ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart>list=shoppingCartMapper.queryLiveOrNot(shoppingCart);
        return list;
    }

    @Override
    public void clean() {
        Long userId=BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userID=BaseContext.getCurrentId();
        shoppingCart.setUserId(userID);

        List<ShoppingCart>list=shoppingCartMapper.queryLiveOrNot(shoppingCart);
        if(list!=null&&list.size()>0){
            ShoppingCart cart=list.get(0);
            Integer number=cart.getNumber();
            if(number==1){
                shoppingCartMapper.cleanById(cart.getId());
            }else {
                cart.setNumber(cart.getNumber()-1);
                shoppingCartMapper.updateShoppingCartByUserId(cart);
            }
        }
    }
}
