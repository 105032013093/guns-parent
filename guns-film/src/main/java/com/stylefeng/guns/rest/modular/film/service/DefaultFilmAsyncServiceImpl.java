package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.FilmAsyncServiceAPI;
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
@Service(interfaceClass = FilmAsyncServiceAPI.class)
public class DefaultFilmAsyncServiceImpl implements FilmAsyncServiceAPI {

    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    private MoocActorTMapper moocActorTMapper;


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




}
