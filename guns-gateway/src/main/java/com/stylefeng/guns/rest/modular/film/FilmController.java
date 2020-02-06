package com.stylefeng.guns.rest.modular.film;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.FilmAsyncServiceAPI;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {

    public static final String IMG_PRE = "http://img.meetingshop.cn/";

    public static final int DEFAULT_NUM = 99;

    @Reference(interfaceClass = FilmServiceAPI.class, check = false)
    private FilmServiceAPI filmServiceAPI;

    // 异步调用API
    @Reference(interfaceClass = FilmAsyncServiceAPI.class, async = true, check = false)
    private FilmAsyncServiceAPI filmAsyncServiceAPI;

    /**
     * 获取首页信息
     *
     * @return
     */
    /*
        API网关：
            1.功能聚合【API聚合】
        优点：
            1.六个接口一次请求，节省了五个http请求
            2.同一个接口对外暴露，降低了前后端分开开发的难度和复杂度
        缺点：
            1.一次获取数据过多
     */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO<FilmIndexVO> getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 获取banner信息
        filmIndexVO.setBanners(filmServiceAPI.getBanners());
        // 获取热映影片
        filmIndexVO.setHotFilms(filmServiceAPI.getHotFilms(true, 8, 1, 1, 99, 99, 99));
        // 获取即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceAPI.getSoonFilms(true, 9, 1, 1, 99, 99, 99));
        // 票房排行榜
        filmIndexVO.setBoxRanking(filmServiceAPI.getBoxRanking());
        // 获取受欢迎榜单
        filmIndexVO.setExpectRanking(filmServiceAPI.getExpectRanking());
        // 获取排名前一百
        filmIndexVO.setTop100(filmServiceAPI.getTop());

        return ResponseVO.succees(filmIndexVO, IMG_PRE);
    }

    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO<FilmConditionVO> getConditionList(@RequestParam(name = "catId", required = false, defaultValue = "99") String catId,
                                                        @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                                        @RequestParam(name = "yearId", required = false, defaultValue = "99") String yearId) {

        FilmConditionVO filmConditionVO = new FilmConditionVO();

        // 类目集合
        List<CatVO> cats = filmServiceAPI.getCats();
        List<CatVO> catResult = new ArrayList<>();
        for (CatVO catVO : cats) {
            // 判断catId是否被选中
            if (catVO.getCatId().equals(catId)) {
                catVO.setActive(true);
            } else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }
        filmConditionVO.setCatInfo(catResult);

        // 片源集合
        List<SourceVO> sources = filmServiceAPI.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        for (SourceVO sourceVO : sources) {
            // 判断catId是否被选中
            if (sourceVO.getSourceId().equals(sourceId)) {
                sourceVO.setActive(true);
            } else {
                sourceVO.setActive(false);
            }
            sourceResult.add(sourceVO);
        }
        filmConditionVO.setSourceInfo(sourceResult);

        // 年代集合
        List<YearVO> years = filmServiceAPI.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        for (YearVO yearVO : years) {
            // 判断catId是否被选中
            if (yearVO.getYearId().equals(yearId)) {
                yearVO.setActive(true);
            } else {
                yearVO.setActive(false);
            }
            yearResult.add(yearVO);
        }
        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.succees(filmConditionVO);
    }

    @RequestMapping(value = "getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO) {

        FilmVO filmVO = null;
        // 根据showType判断查询类型
        switch (filmRequestVO.getShowType()) {
            case 1:
                filmVO = filmServiceAPI.getHotFilms(false, filmRequestVO.getPageSize(),
                        filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getCatId(),
                        filmRequestVO.getSourceId(), filmRequestVO.getYearId());
            case 2:
                filmVO = filmServiceAPI.getSoonFilms(false, filmRequestVO.getPageSize(),
                        filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getCatId(),
                        filmRequestVO.getSourceId(), filmRequestVO.getYearId());
            case 3:
                filmVO = filmServiceAPI.getClassicFilms(filmRequestVO.getPageSize(),
                        filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getCatId(),
                        filmRequestVO.getSourceId(), filmRequestVO.getYearId());
            default:
                filmVO = filmServiceAPI.getHotFilms(false, filmRequestVO.getPageSize(),
                        filmRequestVO.getNowPage(), filmRequestVO.getSortId(), filmRequestVO.getCatId(),
                        filmRequestVO.getSourceId(), filmRequestVO.getYearId());
        }

        return ResponseVO.succees(filmVO.getFilmInfo(), IMG_PRE, filmVO.getNowPage(), filmVO.getTotalPage());
    }

    @RequestMapping(value = "films/{searchParam}", method = RequestMethod.GET)
    public ResponseVO getFilms(@PathVariable("searchParam") String searchParam,
                               int searchType) throws ExecutionException, InterruptedException {

        // 根据searchType判断查询类型
        FilmDetailVO filmDetail = filmServiceAPI.getFilmDetail(searchType, searchParam);

        if (filmDetail == null) {
            return ResponseVO.serviceFail("没有查询影片！");
        } else if (filmDetail.getFilmId() == null || filmDetail.getFilmId().trim().length() == 0) {
            return ResponseVO.serviceFail("没有查询影片！");
        }

        // 查询影片详细信息
        String filmId = filmDetail.getFilmId();

        // 异步调用 getFilmDesc | getImg | getDictActor | getActors
        //FilmDescVO filmDescVO = filmAsyncServiceAPI.getFilmDesc(filmId);
        filmAsyncServiceAPI.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();

        //ImgVO imgVO = filmAsyncServiceAPI.getImg(filmId);
        filmAsyncServiceAPI.getImg(filmId);
        Future<ImgVO> imgVOFuture = RpcContext.getContext().getFuture();

        // 导演
        //ActorVO dictActor = filmAsyncServiceAPI.getDictActor(filmId);
        filmAsyncServiceAPI.getDictActor(filmId);
        Future<ActorVO> dictActorVOFuture = RpcContext.getContext().getFuture();
        // 演员
        //List<ActorVO> actors = filmAsyncServiceAPI.getActors(filmId);
        filmAsyncServiceAPI.getActors(filmId);
        Future<List<ActorVO>> actorsFuture = RpcContext.getContext().getFuture();

        // 组织演员信息
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        // 设置演员属性
        //actorRequestVO.setActors(actors);
        actorRequestVO.setActors(actorsFuture.get());
        // 设置导演属性
        //actorRequestVO.setDirector(dictActor);
        actorRequestVO.setDirector(dictActorVOFuture.get());

        InfoRequestVO infoRequestVO = new InfoRequestVO();
        infoRequestVO.setActors(actorRequestVO);

        //infoRequestVO.setBiography(filmDescVO.getBiography());
        infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());

        //infoRequestVO.setImgVO(imgVO);
        infoRequestVO.setImgVO(imgVOFuture.get());

        infoRequestVO.setFilmId(filmId);

        filmDetail.setInfo04(infoRequestVO);


        return ResponseVO.succees(filmDetail, IMG_PRE);
    }
}
