package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmConditionVO implements Serializable {
    private List<CatVO> catInfo;
    private List<SourceVO> SourceInfo;
    private List<YearVO> yearInfo;
}
