package com.stylefeng.guns.api.user.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoMedel implements Serializable {

    private Integer uuid;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private int sex;
    private String birthday;
    private String lifeState;
    private String biography;
    private String adress;
    private String headAdress;
    private Long beginTime;
    private Long updateTime;

}
