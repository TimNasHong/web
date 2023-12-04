package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.webSocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook=addressBookMapper.queryById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId= BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList=shoppingCartMapper.queryLiveOrNot(shoppingCart);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.insert(orders);
        List<OrderDetail>orderDetailList=new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        shoppingCartMapper.cleanById(userId);

        OrderSubmitVO orderSubmitVO= OrderSubmitVO.builder().id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime()).build();

        return orderSubmitVO;
    }














    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);


      //  调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));




        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderPaidStatus=Orders.PAID;
        Integer OrderStatus=Orders.TO_BE_CONFIRMED;

        LocalDateTime check_out_time=LocalDateTime.now();
        List<Orders>ordersList=orderMapper.queryByStatus(userId);
        for (Orders orders : ordersList) {
            orders.setStatus(Orders.TO_BE_CONFIRMED);
            orders.setPayStatus(Orders.PAID);
            orderMapper.update(orders);
        }
        return vo;
    }


    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        Map map=new HashMap();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+outTradeNo);
        String json= JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

    }

    @Override
    public void userCancelById(Long id) {
        Orders order=orderMapper.getById(id);
        if(order==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(order.getStatus()>2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders=new Orders();
        orders.setId(order.getId());
       /**
        * if(order.getStatus().equals(Orders.CONFIRMED)){
            weChatPayUtil.refund();
        }
        */
       orders.setPayStatus(Orders.REFUND);
       orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
       orders.setCancelReason("用户取消");
       orders.setCancelTime(LocalDateTime.now());
       orderMapper.update(orders);
    }


    @Override
    public OrderVO queryOrderDetail(Long id) {
        Orders order=orderMapper.getById(id);
        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        List<OrderDetail> orderDetailList=orderDetailMapper.getById(order.getId());
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }


    @Override
    public PageResult page(int page, int pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO=new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);
        Page<Orders> orders=orderMapper.queryByOrdersDTO(ordersPageQueryDTO);
        List<OrderVO>orderVOList=new ArrayList<>();
        if(orders!=null&&orders.size()>0){
            for (Orders order : orders) {
                Long userID=order.getUserId();
                OrderVO orderVO=new OrderVO();
                List<OrderDetail> orderDetailList=orderDetailMapper.getById(userID);
                BeanUtils.copyProperties(order,orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(orders.getTotal(),orderVOList);
    }

    @Override
    public void repetition(Long id) {
       Long userID=BaseContext.getCurrentId();
       List<OrderDetail> orderDetailList=orderDetailMapper.getById(id);


       List<ShoppingCart> shoppingCartList=orderDetailList.stream().map(orderDetail -> {
           ShoppingCart shoppingCart=new ShoppingCart();
           BeanUtils.copyProperties(orderDetail,shoppingCart,"id");
           shoppingCart.setUserId(userID);
           shoppingCart.setCreateTime(LocalDateTime.now());
           return shoppingCart;
       }).collect(Collectors.toList());

       shoppingCartMapper.insertBatch(shoppingCartList);
    }


    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders>ordersPage=orderMapper.queryByOrdersDTO(ordersPageQueryDTO);
        List<OrderVO>orderVOList=new ArrayList<>();
        if(ordersPage.size()>0&&ordersPage!=null){
            for (Orders orders : ordersPage) {
                OrderVO orderVO=new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                String orderDishes=getOrderDishesStr(orders);
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(ordersPage.getTotal(),orderVOList);
    }
    private String getOrderDishesStr(Orders orders) {
        List<OrderDetail>orderDetailList=orderDetailMapper.getById(orders.getId());
        List<String>strings=orderDetailList.stream().map(orderDetail -> {
            String detail=orderDetail.getName()+"*"+orderDetail.getNumber();
            return detail;
        }).collect(Collectors.toList());
        return String.join("",strings);
    }


    @Override
    public void deliveryById(Long id) {
       Orders orderDB=orderMapper.getById(id);
       if(orderDB==null||!orderDB.getStatus().equals(Orders.CONFIRMED)){
           throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
       }
       Orders orders=new Orders();
       orders.setId(orderDB.getId());
       orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
       orderMapper.update(orders);
    }


    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders order=Orders.builder().
                id(ordersConfirmDTO.getId()).
                status(Orders.CONFIRMED).build();
        orderMapper.update(order);
    }


    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Orders orderDB=orderMapper.getById(ordersRejectionDTO.getId());

        if(orderDB==null||!orderDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
//        Integer payStatus=orderDB.getPayStatus();
//        if (payStatus == Orders.PAID) {
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    orderDB.getNumber(),
//                    orderDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
//        }

        Orders order=Orders.builder()
                .id(orderDB.getId()).rejectionReason(ordersRejectionDTO.getRejectionReason())
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }


    @Override
    public void complete(Long id) {
        Orders order=orderMapper.getById(id);
        if(order==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders orders=new Orders();
        orders.setId(order.getId());
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }


    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed=orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed=orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress=orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        OrderStatisticsVO orderStatisticsVO=new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        return orderStatisticsVO;
    }


    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        Orders order=orderMapper.getById(ordersCancelDTO.getId());
//        if(order.getPayStatus().equals(Orders.PAID)){
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    order.getNumber(),
//                    order.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
//        }

        Orders orders=Orders.builder()
                .cancelTime(LocalDateTime.now())
                .cancelReason(order.getCancelReason())
                .id(order.getId())
                .status(Orders.CANCELLED)
                .build();
        orderMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        Orders order=orderMapper.getById(id);
        if(order==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map=new HashMap<>();
        map.put("type",2);
        map.put("orderId",order.getId());
        map.put("content","订单号:"+order.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
