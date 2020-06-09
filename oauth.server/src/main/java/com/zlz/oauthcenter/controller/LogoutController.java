package com.zlz.oauthcenter.controller;

import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2020-06-07 09:20
 * @description 退出登陆
 */
@RestController
public class LogoutController {

    @Resource
    private ConsumerTokenServices consumerTokenServices;

    /**
     * 设置token失效
     * @param request
     * @return
     */
    @RequestMapping("/token/logout")
    public String logOut(HttpServletRequest request){
        try {
            System.out.println("111");
            String authorization = request.getHeader("Authorization");
            if(authorization == null){
                return "error";
            }
            String[] s = authorization.split(" ");
            if(s.length != 2){
                return "error";
            }
            String token = s[1];
            System.out.println(token);
            boolean b = consumerTokenServices.revokeToken(token);
            if(!b){
                return "error";
            }

            //清空session
            request.getSession().invalidate();
            return "true";
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

}
