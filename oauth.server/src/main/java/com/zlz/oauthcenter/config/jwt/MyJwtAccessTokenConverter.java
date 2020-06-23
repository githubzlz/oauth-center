package com.zlz.oauthcenter.config.jwt;

import com.zlz.oauthcenter.entity.token.UserVo;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2020-06-23 15:10
 * @description
 */
public class MyJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInformation = new LinkedHashMap<>();
        UserVo userVo = (UserVo) authentication.getPrincipal();
        additionalInformation.put("user_id", userVo.getId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        return super.enhance(accessToken, authentication);
    }
}
