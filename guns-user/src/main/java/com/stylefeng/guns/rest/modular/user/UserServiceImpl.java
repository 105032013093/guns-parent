package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.UserAPI;
import com.stylefeng.guns.api.UserInfoMedel;
import com.stylefeng.guns.api.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = UserAPI.class)
public class UserServiceImpl implements UserAPI {

    @Autowired
    private MoocUserTMapper moocUserTMapper;


    @Override
    public boolean register(UserModel userModel) {
        // 获取注册信息
        // userModel
        // 将注册信息实体转化为数据实体
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());
//        moocUserT.setUserPwd(userModel.getPassword());
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setUserPhone(userModel.getPhone());
        moocUserT.setAddress(userModel.getAdress());

        // 密码进行MD5加密
        String md5Password = MD5Util.encrypt(userModel.getPassword());
        moocUserT.setUserPwd(md5Password);

        // 创建时间和修改时间默认为 current_timestamp
        // 将数据实体存入数据库
        Integer insert = moocUserTMapper.insert(moocUserT);
        if(insert>0){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public int login(String username, String password) {
        // 根据登录帐号获取信息
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(username);
        MoocUserT result = moocUserTMapper.selectOne(moocUserT);
        // 加密密码，与获取信息匹配
        if(result!=null && result.getUuid()>0){
            String md5Password = MD5Util.encrypt(password);
            if(result.getUserPwd().equals(md5Password)){
                return result.getUuid();
            }
        }
        return 0;
    }

    @Override
    public boolean checkUsername(String username) {
        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name",username);
        Integer result = moocUserTMapper.selectCount(entityWrapper);
        if(result!=null && result>0){
            return false;
        }
        return true;
    }

    private UserInfoMedel do2UserInfo(MoocUserT moocUserT){
        // 数据装载
        UserInfoMedel userInfoMedel = new UserInfoMedel();
        userInfoMedel.setUuid(moocUserT.getUuid());
        userInfoMedel.setUsername(moocUserT.getUserName());
        userInfoMedel.setNickname(moocUserT.getNickName());
        userInfoMedel.setEmail(moocUserT.getEmail());
        userInfoMedel.setPhone(moocUserT.getUserPhone());
        userInfoMedel.setSex(moocUserT.getUserSex());
        userInfoMedel.setBirthday(moocUserT.getBirthday());
        userInfoMedel.setLifeState("" + moocUserT.getLifeState());
        userInfoMedel.setBiography(moocUserT.getBiography());
        userInfoMedel.setAdress(moocUserT.getAddress());
        userInfoMedel.setHeadAdress(moocUserT.getHeadUrl());
        userInfoMedel.setBeginTime(moocUserT.getBeginTime().getTime());
        userInfoMedel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        return userInfoMedel;
    }

    @Override
    public UserInfoMedel getUserInfo(int uuid) {
        // 根据主键查询用户信息[MoocUserT]
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        // 对象转化 MoocUserT -> UserInfoModel
        UserInfoMedel userInfoMedel = do2UserInfo(moocUserT);
        // 返回
        return userInfoMedel;
    }

    @Override
    public UserInfoMedel updateUserInfo(UserInfoMedel userInfoMedel) {
        // 将传入数据转化为MoocUserT
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUuid(userInfoMedel.getUuid());
        moocUserT.setNickName(userInfoMedel.getNickname());
        moocUserT.setUserSex(userInfoMedel.getSex());
        moocUserT.setBirthday(userInfoMedel.getBirthday());
        moocUserT.setEmail(userInfoMedel.getEmail());
        moocUserT.setUserPhone(userInfoMedel.getPhone());
        moocUserT.setAddress(userInfoMedel.getAdress());
        moocUserT.setHeadUrl(userInfoMedel.getHeadAdress());
        moocUserT.setBiography(userInfoMedel.getBiography());
        moocUserT.setLifeState(Integer.parseInt( userInfoMedel.getLifeState() ));
        moocUserT.setBeginTime(null);
        moocUserT.setUpdateTime(null);

        // 进行数据库操作
        Integer success = moocUserTMapper.updateById(moocUserT);
        // 修改成功
        if(success==1){
            //查询信息返回
            UserInfoMedel userInfo = getUserInfo(moocUserT.getUuid());
            return userInfo;
        }

        return userInfoMedel;
    }
}
