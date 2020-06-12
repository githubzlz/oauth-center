package com.zlz.oauthcenter;

import com.zlz.oauthcenter.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.ApplicationContext;

/**
 * 程序入口
 * @author zhulinzhong
 */
@SpringCloudApplication
public class OauthCenterApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(OauthCenterApplication.class, args);
        SpringContextUtil.setApplicationContext(applicationContext);
    }

}
