package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceAPI {

    // 获取banner
    List<BannerVO> getBanners();

    // 获取热映影片
    FilmVO getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int catId, int sourceId, int yearId);

    // 获取即将上映影片[受欢迎影片排序]
    FilmVO getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int catId, int sourceId, int yearId);

    // 获取经典影片[受欢迎影片排序]
    FilmVO getClassicFilms(int nums, int nowPage, int sortId, int catId, int sourceId, int yearId);

    // 获取票房排行榜
    List<FilmInfo> getBoxRanking();

    // 获取人气排行榜
    List<FilmInfo> getExpectRanking();

    // 获取Top100
    List<FilmInfo> getTop();

    // 获取影片条件接口
    List<CatVO> getCats();

    // 获取影片资源接口
    List<SourceVO> getSources();

    // 获取影片年代接口
    List<YearVO> getYears();

}
