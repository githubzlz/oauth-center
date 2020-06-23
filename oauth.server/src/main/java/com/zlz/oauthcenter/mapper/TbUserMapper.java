package com.zlz.oauthcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zlz.oauthcenter.entity.user.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/12 16:45
 */
@Mapper
public interface TbUserMapper extends BaseMapper<TbUser> {

    /**
     * 查询用户信息
     * @param username
     * @return
     */
    @Select("select * from tb_user where username=#{username}")
    TbUser selectTbUser(@Param("username")String username);
}
