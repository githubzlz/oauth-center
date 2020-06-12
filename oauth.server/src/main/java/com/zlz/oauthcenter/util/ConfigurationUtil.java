package com.zlz.oauthcenter.util;

import org.springframework.context.annotation.Configuration;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2020-06-12 08:26
 * @description
 */
public class ConfigurationUtil {

    private String gatewayUrl;

    private String myHost;

    private String myPort;

    private Integer gatewayPort;

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getMyHost() {
        return myHost;
    }

    public void setMyHost(String myHost) {
        this.myHost = myHost;
    }

    public String getMyPort() {
        return myPort;
    }

    public void setMyPort(String myPort) {
        this.myPort = myPort;
    }

    public Integer getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(Integer gatewayPort) {
        this.gatewayPort = gatewayPort;
    }
}
