package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    @Reference(
            interfaceClass = OrderServiceAPI.class,
            check = false,
            group = "order2018"
    )
    private OrderServiceAPI orderServiceAPI;

    @Reference(
            interfaceClass = OrderServiceAPI.class,
            check = false,
            group = "order2017"
    )
    private OrderServiceAPI orderServiceAPI2017;

    private static TokenBucket tokenBucket = new TokenBucket();

    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        try {

            if (tokenBucket.getToken()) {


                // 验证已售出票是否为真
                boolean isTrue = orderServiceAPI.isTrueSeats(fieldId + "", soldSeats);

                // 查询是否已经售出
                boolean isNotSold = orderServiceAPI.isNotSoldSeats(fieldId + "", soldSeats);

                if (isTrue && isNotSold) {
                    // 生成订单
                    String userId = CurrentUser.getCurrentUser();
                    if (userId == null || userId.trim().length() == 0) {
                        log.error("根据登录用户获取订单失败，userId不存在");
                        return ResponseVO.serviceFail("根据登录用户获取订单失败，userId不存在");
                    } else {
                        OrderVO orderVO = orderServiceAPI.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                        if (orderVO == null) {
                            log.error("购票失败");
                            return ResponseVO.serviceFail("购票失败");
                        } else {
                            return ResponseVO.succees(orderVO);
                        }
                    }

                } else {
                    return ResponseVO.serviceFail("订单座位编号有误！");
                }
            } else {
                return ResponseVO.serviceFail("购票人数过多，请稍后再试！");
            }
        } catch (Exception e) {
            log.error("购票异常", e);
            return ResponseVO.serviceFail("购票异常");
        }
    }

    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize) {

        // 获取登录信息
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            log.error("根据登录用户获取订单失败，userId不存在");
            return ResponseVO.serviceFail("根据登录用户获取订单失败，userId不存在");
        } else {
            // 查询当前登录人订单
            Page<OrderVO> page = new Page<OrderVO>(nowPage, pageSize);
            Page<OrderVO> result = orderServiceAPI.getOrderByUserId(Integer.parseInt(userId), page);

            Page<OrderVO> result2017 = orderServiceAPI2017.getOrderByUserId(Integer.parseInt(userId), page);

            // 合并结果
            int totalPages = (int) (result.getPages() + result2017.getPages());
            List<OrderVO> orderList = new ArrayList<>();

            orderList.addAll(result.getRecords());
            orderList.addAll(result2017.getRecords());

            return ResponseVO.succees(orderList, "", nowPage, totalPages);
        }
    }

}

