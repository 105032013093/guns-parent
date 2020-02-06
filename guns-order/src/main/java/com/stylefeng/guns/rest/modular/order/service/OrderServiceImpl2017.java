package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2017TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceAPI.class, group = "order2017")
public class OrderServiceImpl2017 implements OrderServiceAPI {


    @Autowired
    private MoocOrder2017TMapper moocOrder2017TMapper;
    @Reference(interfaceClass = CinemaServiceAPI.class, check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FTPUtil ftpUtil;

    @Override
    public boolean isTrueSeats(String fieldId, String seats) {

        // 根据fieldId获取影院找到对应座位位置图
        String seatPath = moocOrder2017TMapper.getSeatsByFieldId(fieldId);
        // 获取FTP服务器文本
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);

        String ids = jsonObject.get("ids").toString();

        String[] idArrs = ids.split(",");
        String[] seatArrs = seats.split(",");

        if (seatArrs.length == 0 || seatArrs.length > idArrs.length) {
            log.error("购买影片订单传入座位号异常");
            return false;
        }

        int count = 0;
        // 判断seats是否为真
        for (String seat : seatArrs) {
            for (String id : idArrs) {
                if (seat.equalsIgnoreCase(id)) {
                    count++;
                }
            }
        }

        if (count == seatArrs.length) {
            return true;
        } else {
            log.error("购买影片订单传入座位号异常");
            return false;
        }
    }

    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);

        List<MoocOrder2017T> list = moocOrder2017TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");

        for (MoocOrder2017T moocOrderT : list) {
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for (String id : ids) {
                for (String seat : seatArrs) {
                    if (id.equalsIgnoreCase(seat)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        String uuid = UUIDUtil.getUuid();
        // 获取影片信息
        FilmInfoVO filmInfoVO = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());
        // 获取影院信息
        OrderQueryVO orderQueryVO = cinemaServiceAPI.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.valueOf(orderQueryVO.getFilmPrice());

        // 求订单总金额
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrder2017T moocOrder2017T = new MoocOrder2017T();
        moocOrder2017T.setUuid(uuid);
        moocOrder2017T.setCinemaId(cinemaId);
        moocOrder2017T.setFieldId(fieldId);
        moocOrder2017T.setFilmId(filmId);
        moocOrder2017T.setSeatsIds(soldSeats);
        moocOrder2017T.setSeatsName(seatsName);
        moocOrder2017T.setFilmPrice(filmPrice);
        moocOrder2017T.setOrderPrice(totalPrice);
        moocOrder2017T.setOrderUser(userId);

        Integer insert = moocOrder2017TMapper.insert(moocOrder2017T);
        if (insert > 0) {
            OrderVO orderVO = moocOrder2017TMapper.getOrderInfoById(uuid);
            if (orderVO == null || orderVO.getOrderId().trim() == null) {
                log.error("插入订单失败，数据插入成功，查询出错！订单编号为{}", uuid);
                return null;
            }
            return orderVO;
        } else {
            log.error("插入订单失败");
            return null;
        }

    }

    private Double getTotalPrice(int solds, double filmPrice) {
        BigDecimal bdSolds = new BigDecimal(solds);
        BigDecimal bdFilmPrice = new BigDecimal(filmPrice);
        BigDecimal result = bdSolds.multiply(bdFilmPrice);

        // 四舍五入，保留小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if (userId == null) {
            log.error("订单查询失败————》userId获取失败");
            return null;

        } else {
            List<OrderVO> orderList = moocOrder2017TMapper.getOrdersByUserId(userId, page);
            if (orderList == null || orderList.size() == 0) {
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            } else {
                // 获取订单总数
                EntityWrapper<MoocOrder2017T> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user", userId);
                Integer counts = moocOrder2017TMapper.selectCount(entityWrapper);
                result.setTotal(counts);
                result.setRecords(orderList);
                return result;
            }
        }
    }

    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId == null) {
            log.error("获取已售座位列表失败，未传入任何场次编号");
            return "";
        } else {
            String soldSeatsByFieldId = moocOrder2017TMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }
}
