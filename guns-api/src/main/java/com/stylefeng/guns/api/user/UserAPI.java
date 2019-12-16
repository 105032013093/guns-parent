package com.stylefeng.guns.api.user;

import com.stylefeng.guns.api.user.vo.UserInfoMedel;
import com.stylefeng.guns.api.user.vo.UserModel;

public interface UserAPI {

    int login(String username, String password);

    boolean register(UserModel userModel);

    boolean checkUsername(String username);

    UserInfoMedel getUserInfo(int uuid);

    UserInfoMedel updateUserInfo(UserInfoMedel userInfoMedel);
}
