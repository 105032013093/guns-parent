package com.stylefeng.guns.rest.common;

import com.stylefeng.guns.api.UserInfoMedel;

public class CurrentUser {
    /*
    // 线程绑定的储存空间
    private static final ThreadLocal<UserInfoMedel> threadLocal = new ThreadLocal<>();

    // 将用户信息放入存储空间
    public static  void saveUserIfo(UserInfoMedel userInfoMedel){
        threadLocal.set(userInfoMedel);
    }

    // 将用户信息取出存储空间
    public static UserInfoMedel getCurrentUser() {
        return threadLocal.get();
     */

    // 线程绑定的储存空间
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // 将用户信息放入存储空间
    public static  void saveUserId(String userId){
        threadLocal.set(userId);
    }

    // 将用户信息取出存储空间
    public static String getCurrentUser() {
        return threadLocal.get();


    }
}
