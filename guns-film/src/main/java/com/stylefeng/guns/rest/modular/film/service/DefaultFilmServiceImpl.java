package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocBannerTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocBannerT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmServiceAPI.class)
public class DefaultFilmServiceImpl implements FilmServiceAPI {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;


    @Override
    public List<BannerVO> getBanners() {

        List<BannerVO> result = new ArrayList<>();

        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);

        for(MoocBannerT moocBannerT : moocBanners){
            BannerVO bannerVO =new BannerVO();
            bannerVO.setBannerId(moocBannerT.getUuid()+"");
            bannerVO.setBannerAdress(moocBannerT.getBannerAddress());
            bannerVO.setBannerUrl(moocBannerT.getBannerUrl());

            result.add(bannerVO);
        }
        return result;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums) {
        return null;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums) {
        return null;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        return null;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        return null;
    }

    @Override
    public List<FilmInfo> getTop() {
        return null;
    }
}
