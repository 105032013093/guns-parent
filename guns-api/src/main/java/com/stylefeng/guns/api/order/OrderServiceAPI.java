package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;

import java.util.List;

public interface OrderServiceAPI {


    // 验证已售出票是否为真
    boolean isTrueSeats(String fieldId, String seats);

    // 查询是否已经售出
    boolean isNotSoldSeats(String fieldId, String seats);

    // 生成订单
    OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    // 查询当前登录人订单
    Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page);

    // 根据影院FieldId获取已经销售座位编号
    String getSoldSeatsByFieldId(Integer fieldId);

}
