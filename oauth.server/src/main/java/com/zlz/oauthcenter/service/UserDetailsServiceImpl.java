package com.zlz.oauthcenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zlz.oauthcenter.entity.token.UserVo;
import com.zlz.oauthcenter.entity.user.TbPermission;
import com.zlz.oauthcenter.entity.user.TbRolePermission;
import com.zlz.oauthcenter.entity.user.TbUser;
import com.zlz.oauthcenter.entity.user.TbUserRole;
import com.zlz.oauthcenter.mapper.TbPermissionMapper;
import com.zlz.oauthcenter.mapper.TbRolePermissionMapper;
import com.zlz.oauthcenter.mapper.TbUserMapper;
import com.zlz.oauthcenter.mapper.TbUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/12 16:37
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TbUserMapper tbUserMapper;
    @Autowired
    private TbUserRoleMapper tbUserRoleMapper;
    @Autowired
    private TbPermissionMapper tbPermissionMapper;
    @Autowired
    private TbRolePermissionMapper tbRolePermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<TbUser> query1 = new QueryWrapper<>();
        query1.eq("username", username);
        TbUser user = tbUserMapper.selectOne(query1);

        QueryWrapper<TbUserRole> query2 = new QueryWrapper<>();
        query2.eq("user_id", user.getId());
        List<TbUserRole> tbUserRoles = tbUserRoleMapper.selectList(query2);
        List<Long> roles = new ArrayList<>();
        for (TbUserRole role : tbUserRoles) {
            roles.add(role.getRoleId());
        }
        QueryWrapper<TbRolePermission> query3 = new QueryWrapper<>();
        query3.in("role_id", roles);
        List<TbRolePermission> tbRolePermissions = tbRolePermissionMapper.selectList(query3);

        List<Long> permissions = new ArrayList<>();
        for (TbRolePermission tbRolePermission : tbRolePermissions) {
            permissions.add(tbRolePermission.getPermissionId());
        }
        QueryWrapper<TbPermission> query4 = new QueryWrapper<>();
        query4.in("id", permissions);
        List<TbPermission> tbPermissions = tbPermissionMapper.selectList(query4);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (TbPermission tbPermission : tbPermissions) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(tbPermission.getEname());
            grantedAuthorities.add(grantedAuthority);
        }
        return new UserVo(user.getUsername(), user.getPassword(), grantedAuthorities, user.getId());
    }
}
