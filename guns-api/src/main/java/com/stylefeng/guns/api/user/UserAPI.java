package com.stylefeng.guns.api.user;

public interface UserAPI {

    int login(String username, String password);

    boolean register(UserModel userModel);

    boolean checkUsername(String username);

    UserInfoMedel getUserInfo(int uuid);

    UserInfoMedel updateUserInfo(UserInfoMedel userInfoMedel);
}
