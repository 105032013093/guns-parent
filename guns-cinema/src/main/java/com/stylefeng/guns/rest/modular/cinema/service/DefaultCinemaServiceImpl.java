package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceAPI.class)
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {

    //1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO){
        return null;
    }

    //2、根据条件获取品牌列表
    @Override
    public List<BrandVO> getBrands(int brandId){
        return null;
    }

    //3、获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId){
        return null;
    }

    //4、获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallTypeId){
        return null;
    }

    //5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId){
        return null;
    }

    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public FilmInfoVO getFilmInfoByCinemaId(int CinemaId){
        return null;
    }

    //7、根据放映场次ID获取放映信息
    @Override
    public FilmFieldVO getFilmFieldInfo(int fieldId){
        return null;
    }

    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId){
        return null;
    }
}
