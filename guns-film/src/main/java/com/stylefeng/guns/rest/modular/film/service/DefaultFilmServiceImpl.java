package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmServiceAPI.class)
public class DefaultFilmServiceImpl implements FilmServiceAPI {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;
    @Autowired
    private MoocFilmTMapper moocFilmTMapper;
    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;
    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;
    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;
    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    private MoocActorTMapper moocActorTMapper;


    @Override
    public List<BannerVO> getBanners() {

        List<BannerVO> result = new ArrayList<>();

        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);

        for (MoocBannerT moocBannerT : moocBanners) {
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerId(moocBannerT.getUuid() + "");
            bannerVO.setBannerAdress(moocBannerT.getBannerAddress());
            bannerVO.setBannerUrl(moocBannerT.getBannerUrl());

            result.add(bannerVO);
        }
        return result;
    }

    private List<FilmInfo> getFileInfos(List<MoocFilmT> moocFilms) {
        List<FilmInfo> filmInfos = new ArrayList<>();
        for (MoocFilmT moocFilmT : moocFilms) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setFilmId(moocFilmT.getUuid() + "");
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setImgAdress(moocFilmT.getImgAddress());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setScore(moocFilmT.getFilmScore());
            // 插入结果集
            filmInfos.add(filmInfo);

        }

        return filmInfos;

    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int catId, int sourceId, int yearId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();
        // 限制为热映影片
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        // 判断是否首页需要内容
        if (isLimit) {
            // 如果是限制条数
            Page<MoocFilmT> page = new Page<>(1, nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfos
            filmInfos = getFileInfos(moocFilms);

            filmVO.setFileNum(filmInfos.size());
            filmVO.setFilmInfo(filmInfos);
        } else {
            // 如果不是为列表页
            // Page<MoocFilmT> page = new Page<>(nowPage, nums);

            Page<MoocFilmT> page = null;
            // 根据sortId不同，来组织不同的Page对象
            // 排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
            switch (sortId) {
                case 1:
                    page = new Page<>(nowPage, nums, "film_box_office");
                    break;
                case 2:
                    page = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, nums, "film_score");
                    break;
                default:
                    page = new Page<>(nowPage, nums, "film_box_office");
                    break;
            }

            if (catId != 99) {
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_date", yearId);
            }

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfos
            filmInfos = getFileInfos(moocFilms);

            filmVO.setFileNum(filmInfos.size());
            filmVO.setFilmInfo(filmInfos);

            // 获取总页数
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPages = (totalCounts / nums) + 1;
            filmVO.setFileNum(totalPages);

            filmVO.setNowPage(nowPage);
            filmVO.setTotalPage(totalPages);
        }
        return filmVO;
    }


    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int catId, int sourceId, int yearId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();
        // 限制为即将上映影片
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");
        // 判断是否首页需要内容
        if (isLimit) {
            // 如果是限制条数
            Page<MoocFilmT> page = new Page<>(1, nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfos
            filmInfos = getFileInfos(moocFilms);

            filmVO.setFileNum(filmInfos.size());
            filmVO.setFilmInfo(filmInfos);
        } else {
            // 如果不是为列表页
            // Page<MoocFilmT> page = new Page<>(nowPage, nums);

            Page<MoocFilmT> page = null;
            // 根据sortId不同，来组织不同的Page对象
            // 排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
            switch (sortId) {
                case 1:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                default:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
            }

            if (catId != 99) {
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_date", yearId);
            }

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfos
            filmInfos = getFileInfos(moocFilms);

            filmVO.setFileNum(filmInfos.size());
            filmVO.setFilmInfo(filmInfos);

            // 获取总页数
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPages = (totalCounts / nums) + 1;
            filmVO.setFileNum(totalPages);

            filmVO.setNowPage(nowPage);
            filmVO.setTotalPage(totalPages);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int catId, int sourceId, int yearId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();
        // 限制为经典影片
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "3");

        // Page<MoocFilmT> page = new Page<>(nowPage, nums);

        Page<MoocFilmT> page = null;
        // 根据sortId不同，来组织不同的Page对象
        // 排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
        switch (sortId) {
            case 1:
                page = new Page<>(nowPage, nums, "film_box_office");
                break;
            case 2:
                page = new Page<>(nowPage, nums, "film_time");
                break;
            case 3:
                page = new Page<>(nowPage, nums, "film_score");
                break;
            default:
                page = new Page<>(nowPage, nums, "film_box_office");
                break;
        }

        if (catId != 99) {
            String catStr = "%#" + catId + "#%";
            entityWrapper.like("film_cats", catStr);
        }
        if (sourceId != 99) {
            entityWrapper.eq("film_source", sourceId);
        }
        if (yearId != 99) {
            entityWrapper.eq("film_date", yearId);
        }

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
        // 组织filmInfos
        filmInfos = getFileInfos(moocFilms);

        filmVO.setFileNum(filmInfos.size());
        filmVO.setFilmInfo(filmInfos);

        // 获取总页数
        int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
        int totalPages = (totalCounts / nums) + 1;
        filmVO.setFileNum(totalPages);

        filmVO.setNowPage(nowPage);
        filmVO.setTotalPage(totalPages);

        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        List<FilmInfo> filmInfos = new ArrayList<>();
        // 限制为热映影片
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");

        Page<MoocFilmT> page = new Page<>(1, 10, "film_box_office");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
        // 组织filmInfos
        filmInfos = getFileInfos(moocFilms);

        return filmInfos;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        List<FilmInfo> filmInfos = new ArrayList<>();
        // 限制为即将上映影片
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");

        Page<MoocFilmT> page = new Page<>(1, 10, "film_preSaleNum");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
        // 组织filmInfos
        filmInfos = getFileInfos(moocFilms);

        return filmInfos;
    }

    @Override
    public List<FilmInfo> getTop() {
        List<FilmInfo> filmInfos = new ArrayList<>();
        // 限制为经典影片
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");

        Page<MoocFilmT> page = new Page<>(1, 10, "film_score");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
        // 组织filmInfos
        filmInfos = getFileInfos(moocFilms);

        return filmInfos;
    }

    private MoocFilmInfoT getFilmInfo(String filmId) {
        MoocFilmInfoT moocFilmInfoT = new MoocFilmInfoT();
        moocFilmInfoT.setFilmId(filmId);
        moocFilmInfoT = moocFilmInfoTMapper.selectOne(moocFilmInfoT);
        return moocFilmInfoT;

    }

    @Override
    public FilmDescVO getFilmDesc(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        FilmDescVO filmDescVO = new FilmDescVO();
        filmDescVO.setBiography(moocFilmInfoT.getBiography());
        filmDescVO.setFilmId(moocFilmInfoT.getFilmId());

        return filmDescVO;
    }

    @Override
    public ImgVO getImg(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        String filmImgStr = moocFilmInfoT.getFilmImgs();

        String[] filmImgs = filmImgStr.split(",");

        ImgVO imgVO = new ImgVO();

        if (filmImgs.length == 5) {
            imgVO.setMainImg(filmImgs[0]);
            imgVO.setImg01(filmImgs[1]);
            imgVO.setImg02(filmImgs[2]);
            imgVO.setImg03(filmImgs[3]);
            imgVO.setImg04(filmImgs[4]);
        }
        return imgVO;
    }

    @Override
    public ActorVO getDictActor(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);
        Integer directorId = moocFilmInfoT.getDirectorId();

        MoocActorT moocActorT = moocActorTMapper.selectById(directorId);

        ActorVO actorVO = new ActorVO();

        actorVO.setDirectorName(moocActorT.getActorName());
        actorVO.setImgAddress(moocActorT.getActorImg());
        actorVO.setImgAddress(moocActorT.getActorImg());


        return actorVO;
    }

    @Override
    public List<ActorVO> getActors(String filmId) {
        List<ActorVO> actors = moocActorTMapper.getActors(filmId);

        return actors;
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> cats = new ArrayList<>();
        // 查询实体对象
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);
        for (MoocCatDictT moocCatDictT : moocCats) {
            CatVO catVO = new CatVO();
            // 转化为业务对象
            catVO.setCatId(moocCatDictT.getUuid() + "");
            catVO.setCatName(moocCatDictT.getShowName());

            cats.add(catVO);
        }

        return cats;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> Sources = new ArrayList<>();
        // 查询实体对象
        List<MoocSourceDictT> moocSources = moocSourceDictTMapper.selectList(null);
        for (MoocSourceDictT moocSourceDictT : moocSources) {
            SourceVO sourceVO = new SourceVO();
            // 转化为业务对象
            sourceVO.setSourceId(moocSourceDictT.getUuid() + "");
            sourceVO.setSourceName(moocSourceDictT.getShowName());

            Sources.add(sourceVO);
        }

        return Sources;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> years = new ArrayList<>();
        // 查询实体对象
        List<MoocYearDictT> moocYears = moocYearDictTMapper.selectList(null);
        for (MoocYearDictT moocYearDictT : moocYears) {
            YearVO yearVO = new YearVO();
            // 转化为业务对象
            yearVO.setYearId(moocYearDictT.getUuid() + "");
            yearVO.setYearName(moocYearDictT.getShowName());

            years.add(yearVO);
        }

        return years;
    }

    @Override
    public FilmDetailVO getFilmDetail(int searchType, String searchParam) {
        FilmDetailVO filmDetailVO = null;
        // 判断searchType 1|按照名称;2|按照ID
        if (searchType == 1) {
            filmDetailVO = moocFilmTMapper.getFilmDetailByName("%" + searchParam + "%");
        } else {
            filmDetailVO = moocFilmTMapper.getFilmDetailById(searchParam);
        }
        return filmDetailVO;
    }
}
