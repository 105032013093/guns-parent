package com.stylefeng.guns.api;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmAsyncServiceAPI {

    // 获取影片相关其他信息
    // 获取影片描述信息
    FilmDescVO getFilmDesc(String filmId);

    // 获取图片信息
    ImgVO getImg(String filmId);

    // 获取导演信息
    ActorVO getDictActor(String filmId);

    // 获取演员信息
    List<ActorVO> getActors(String filmId);

}
