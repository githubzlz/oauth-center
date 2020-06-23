package com.zlz.oauthcenter.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色
 * @author zhulinzhong
 */
@Data
public class TbRole implements Serializable {
    private static final long serialVersionUID = -8262507558652471121L;
    private Long id;

    /**
     * 父角色
     */
    private Long parentId;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色英文名称
     */
    @TableField("ename")
    private String ename;

    /**
     * 备注
     */
    private String description;
}