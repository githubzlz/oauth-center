package com.zlz.oauthcenter.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户权限
 * @author zhulinzhong
 */
@Data
public class TbPermission implements Serializable {
    private static final long serialVersionUID = -3065895074477720147L;
    private Long id;

    /**
     * 父权限
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限英文名称
     */
    @TableField("ename")
    private String ename;

    /**
     * 授权路径
     */
    private String url;

    /**
     * 备注
     */
    private String description;

}