package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceAPI.class, executes = 10)
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {

    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;
    @Autowired
    private MoocFieldTMapper moocFieldTMapper;
    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;


    //1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {
        // 初始化Page对象
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());
        // 条件查询
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if (cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType() != 99) {
            entityWrapper.like("hall_ids", "%#" + cinemaQueryVO.getHallType() + "#%");
        }
        // 数据层获取
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, entityWrapper);

        // 业务实体
        List<CinemaVO> cinemas = new ArrayList<>();
        for (MoocCinemaT moocCinemaT : moocCinemaTS) {

            CinemaVO cinemaVO = new CinemaVO();

            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVO.setUuid(moocCinemaT.getUuid() + "");
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice() + "");

            cinemas.add(cinemaVO);
        }

        // 初始化返回对象
        long counts = moocCinemaTMapper.selectCount(entityWrapper);
        // 组织返回对象
        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);

        return result;
    }

    //2、根据条件获取品牌列表
    @Override
    public List<BrandVO> getBrands(int brandId) {

        // 是否全部查询 FLAG:false|按brandId查询;true|全部查询
        boolean flag = false;

        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);

        if (brandId == 99 || moocBrandDictT == null || moocBrandDictT.getUuid() == null) {
            flag = true;
        }

        List<MoocBrandDictT> moocBrandDictTS = moocBrandDictTMapper.selectList(null);

        List<BrandVO> brands = new ArrayList<>();

        for (MoocBrandDictT brand : moocBrandDictTS) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(brand.getUuid() + "");
            brandVO.setBrandName(brand.getShowName());
            if (flag) {
                if (brand.getUuid() == 99) {
                    brandVO.setActive(true);
                }
            } else {
                if (brand.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }
            brands.add(brandVO);
        }

        return brands;
    }

    //3、获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId) {
        // 是否全部查询 FLAG:false|按brandId查询;true|全部查询
        boolean flag = false;

        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);

        if (areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null) {
            flag = true;
        }

        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);

        List<AreaVO> areas = new ArrayList<>();

        for (MoocAreaDictT area : moocAreaDictTS) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaId(area.getUuid() + "");
            areaVO.setAreaName(area.getShowName());
            if (flag) {
                if (area.getUuid() == 99) {
                    areaVO.setActive(true);
                }
            } else {
                if (area.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }
            areas.add(areaVO);
        }

        return areas;
    }

    //4、获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallTypeId) {
        // 是否全部查询 FLAG:false|按brandId查询;true|全部查询
        boolean flag = false;

        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallTypeId);

        if (hallTypeId == 99 || moocHallDictT == null || moocHallDictT.getUuid() == null) {
            flag = true;
        }

        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);

        List<HallTypeVO> halls = new ArrayList<>();

        for (MoocHallDictT hallDict : moocHallDictTS) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHallTypeId(hallDict.getUuid() + "");
            hallTypeVO.setHallTypeName(hallDict.getShowName());
            if (flag) {
                if (hallDict.getUuid() == 99) {
                    hallTypeVO.setActive(true);
                }
            } else {
                if (hallDict.getUuid() == hallTypeId) {
                    hallTypeVO.setActive(true);
                }
            }
            halls.add(hallTypeVO);
        }

        return halls;
    }

    //5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {


        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);

        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid() + "");
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaAdress(moocCinemaT.getCinemaAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        return cinemaInfoVO;
    }

    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }

    //7、根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {

        HallInfoVO hallInfo = moocFieldTMapper.getHallInfo(fieldId);
        return hallInfo;
    }

    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {

        FilmInfoVO filmInfo = moocFieldTMapper.getFilmInfoById(fieldId);

        return filmInfo;
    }

    @Override
    public OrderQueryVO getOrderNeeds(int fileId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fileId);
        orderQueryVO.setCinemaId(moocFieldT.getCinemaId() + "");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice() + "");

        return orderQueryVO;
    }
}
