package com.zlz.oauthcenter.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 用户信息
 * @author zhulinzhong
 */
@TableName("tb_user")
public class TbUser implements Serializable {
    private static final long serialVersionUID = -4540933489036634187L;

    private Long id;

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}