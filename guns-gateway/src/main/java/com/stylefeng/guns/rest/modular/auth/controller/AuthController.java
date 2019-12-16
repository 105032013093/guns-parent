package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Reference(interfaceClass = UserAPI.class,check = false)
    private UserAPI userAPI;

    @RequestMapping(value = "${jwt.auth-path}")
    public ResponseVO createAuthenticationToken(AuthRequest authRequest) {

        boolean validate = true;
        // 修改guns内置的用户名密码登录验证机制，自己实现
        int userId = userAPI.login(authRequest.getUserName(), authRequest.getPassword());

        if(userId==0){
            validate = false;
        }

        if (validate) {
            // 生成randomKey和token
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken("" + userId , randomKey);
            // 返回值

//            return ResponseEntity.ok(new AuthResponse(token, randomKey));
            return ResponseVO.succees(new AuthResponse(token, randomKey));

        } else {
//            throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
            return ResponseVO.serviceFail("用户名或密码错误！");
        }
    }
}
