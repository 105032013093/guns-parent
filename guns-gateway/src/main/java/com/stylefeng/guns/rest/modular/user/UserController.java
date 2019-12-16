package com.stylefeng.guns.rest.modular.user;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoMedel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference(interfaceClass = UserAPI.class,check = false)
    private UserAPI userAPI;

    /**
     * 用户注册接口
     * @param userModel
     * @return
     */
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResponseVO register(UserModel userModel){

        if(userModel.getUsername() == null || userModel.getUsername().trim().length()==0){
            return ResponseVO.serviceFail("用户名不能为空！");
        }

        if(userModel.getPassword() == null || userModel.getPassword().trim().length()==0){
            return ResponseVO.serviceFail("密码不能为空！");
        }

        boolean isSuccess = userAPI.register(userModel);

        if(isSuccess){
            return ResponseVO.succees("注册成功!");
        } else {
            return ResponseVO.serviceFail("注册失败！");
        }
    }

    /**
     * 检查用户名是否存在
     * @param username
     * @return
     */
    @RequestMapping(value = "check",method = RequestMethod.POST)
    public ResponseVO check(String username){

        if(username != null || username.trim().length()>0){
            // 返回true表示用户名可用
            boolean notExists = userAPI.checkUsername(username);
            if(notExists){
                return ResponseVO.succees("用户名不存在！");
            } else {
                return ResponseVO.serviceFail("用户名已存在！");
            }
        } else {
            return ResponseVO.serviceFail("用户名不能为空！");
        }
    }

    /**
     * 用户退出
     * @return
     */
    @RequestMapping(value = "logout",method = RequestMethod.GET)
    public ResponseVO logout(){
        /*
        应用：
            1.前端存储JWT 7天  ——> JWT的刷新
            2.服务器会存储活动用户信息  30分钟
            3.JWT的userId为KEY，查找活跃用户
        退出：
            1.前端删除JWT
            2.后端服务器删除活跃用户缓存
        目前实现：
            1.前端删除JWT
         */
        return ResponseVO.succees("用户退出成功！");
    }


    @RequestMapping(value = "getUserInfo",method = RequestMethod.GET)
    public ResponseVO getUserInfo(){
        // 获取当前登录用户
        String userId = CurrentUser.getCurrentUser();
        if(userId!=null && userId.trim().length()>0){
            // 查询信息
            int uuid = Integer.parseInt(userId);
            UserInfoMedel userInfo = userAPI.getUserInfo(uuid);
            if(userInfo!=null){
                return  ResponseVO.succees(userInfo);
            } else {
                return ResponseVO.serviceFail("用户信息查询失败！");
            }
        } else {
            return ResponseVO.serviceFail("用户未登录！");
        }
    }


    @RequestMapping(value = "updateUserInfo",method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoMedel userInfoMedel){
        // 获取当前登录用户
        String userId = CurrentUser.getCurrentUser();
        if(userId!=null && userId.trim().length()>0){
            // 查询信息
            int uuid = Integer.parseInt(userId);
            // 判断当前登录用户ID与要修改用户信息ID是否一致
            if(uuid != userInfoMedel.getUuid()){
                return ResponseVO.serviceFail("请修改您个人信息！");
            }
            UserInfoMedel userInfo = userAPI.updateUserInfo(userInfoMedel);
            if(userInfo!=null){
                return  ResponseVO.succees(userInfo);
            } else {
                return ResponseVO.serviceFail("用户信息修改失败！");
            }
        } else {
            return ResponseVO.serviceFail("用户未登录！");
        }
    }

}
