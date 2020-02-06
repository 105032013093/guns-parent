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
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2018TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2018T;
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
@Service(interfaceClass = OrderServiceAPI.class, group = "order2018")
public class OrderServiceImpl2018 implements OrderServiceAPI {


    @Autowired
    private MoocOrder2018TMapper moocOrder2018TMapper;
    @Reference(interfaceClass = CinemaServiceAPI.class, check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FTPUtil ftpUtil;

    @Override
    public boolean isTrueSeats(String fieldId, String seats) {

        // 根据fieldId获取影院找到对应座位位置图
        String seatPath = moocOrder2018TMapper.getSeatsByFieldId(fieldId);
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

        List<MoocOrder2018T> list = moocOrder2018TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");

        for (MoocOrder2018T moocOrderT : list) {
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

        MoocOrder2018T moocOrder2018T = new MoocOrder2018T();
        moocOrder2018T.setUuid(uuid);
        moocOrder2018T.setCinemaId(cinemaId);
        moocOrder2018T.setFieldId(fieldId);
        moocOrder2018T.setFilmId(filmId);
        moocOrder2018T.setSeatsIds(soldSeats);
        moocOrder2018T.setSeatsName(seatsName);
        moocOrder2018T.setFilmPrice(filmPrice);
        moocOrder2018T.setOrderPrice(totalPrice);
        moocOrder2018T.setOrderUser(userId);

        Integer insert = moocOrder2018TMapper.insert(moocOrder2018T);
        if (insert > 0) {
            OrderVO orderVO = moocOrder2018TMapper.getOrderInfoById(uuid);
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
            List<OrderVO> orderList = moocOrder2018TMapper.getOrdersByUserId(userId, page);
            if (orderList == null || orderList.size() == 0) {
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            } else {
                // 获取订单总数
                EntityWrapper<MoocOrder2018T> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user", userId);
                Integer counts = moocOrder2018TMapper.selectCount(entityWrapper);
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
            String soldSeatsByFieldId = moocOrder2018TMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }
}
