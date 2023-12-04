package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;


    @Scheduled(cron="* 1 * * * ? ")
    //@Scheduled(cron="0/5 * * * * ? ")
    public void orderPayTimeOver(){
        LocalDateTime localDateTime=LocalDateTime.now().plusMinutes(-15);
        log.info("定时清理未支付的订单：{}",localDateTime);
        List<Orders> ordersList=orderMapper.getStatusIsPayTimeOut(Orders.PENDING_PAYMENT,localDateTime);
        if(ordersList!=null &&ordersList.size()>0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时!");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    @Scheduled(cron="0 0 1 * * ? ")
    //@Scheduled(cron="1/5 * * * * ? ")
    public void orderDelivery(){
        LocalDateTime localDateTime=LocalDateTime.now().plusHours(-1);
        log.info("定时清理在外送的订单：{}",localDateTime);
        List<Orders> ordersList=orderMapper.getStatusIsPayTimeOut(Orders.DELIVERY_IN_PROGRESS,localDateTime);
        if(ordersList!=null &&ordersList.size()>0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
