package com.stylefeng.guns.rest.modular.film;


import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/film/")
public class FilmController {

    /**
     * 获取首页信息
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
    @RequestMapping(value = "getIndex",method = RequestMethod.GET)
    public ResponseVO getIndex(){

        // 获取banner信息

        // 获取热映影片


        // 获取即将上映的电影

        // 票房排行榜

        // 获取受欢迎榜单


        // 获取排名前一百
        return null;
    }
}
