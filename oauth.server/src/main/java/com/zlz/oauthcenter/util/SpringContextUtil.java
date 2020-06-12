package com.zlz.oauthcenter.util;

import org.springframework.context.ApplicationContext;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2020-06-12 16:49
 * @description
 */
public class SpringContextUtil {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 返回指定bean的实例
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId) {
        return applicationContext.getBean(beanId);
    }

}
