package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    
@Update("update orders set status=#{orderStatus},pay_status=#{orderPaidStatus},checkout_time=#{check_out_time} where user_id=#{userId}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long userId);
@Select("select *from orders where id=#{id}")
    Orders getById(Long id);




    Page<Orders> queryByOrdersDTO(OrdersPageQueryDTO ordersPageQueryDTO);
@Select("select count(id) from orders where status=#{cancelled}")
    Integer countStatus(Integer cancelled);
@Select("select*from orders where user_id=#{userId} and status=1")
    List<Orders> queryByStatus(Long userId);
@Select("select*from orders where status=#{pendingPayment} and order_time<#{localDateTime}")
    List<Orders> getStatusIsPayTimeOut(Integer pendingPayment, LocalDateTime localDateTime);

    Double getTurnoverByDate(Map map);
}
