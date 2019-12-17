package com.stylefeng.guns.rest.modular.film;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/film/")
public class FilmController {

    public static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceAPI.class)
    private FilmServiceAPI filmServiceAPI;

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
        filmIndexVO.setHotFilms(filmServiceAPI.getHotFilms(true, 8));
        // 获取即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceAPI.getSoonFilms(true, 9));
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
        // 标志位
        boolean flag = false;
        // 类型集合
        List<CatVO> cats = filmServiceAPI.getCats();
        List<CatVO> catResult = filmServiceAPI.getCats();
        for(CatVO catVO : cats) {
            // 判断catId是否被选中
            if(catVO.getCatId().equals(catId)) {
                flag = true;
                catVO.setActive(true);
            }
            catResult.add(catVO);
            // 如果不存在则将【全部】置为active

        }

        // 片源集合

        // 年代集合
        return null;
    }
}
