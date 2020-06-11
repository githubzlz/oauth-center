package com.zlz.oauthcenter.config;

import com.zlz.oauthcenter.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author zhulinzhong
 * @version 1.0 CreateTime:2019/10/18 11:41
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //静态资源不拦截
        web.ignoring().antMatchers("/js/**","/css/**","/favicon-20190918085337900.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin()
                .loginPage("https://www.zlztsb.com:80/oauth-server/auth/login")
                .loginProcessingUrl("/oauth/form")
                .and()
                .requestCache().requestCache(new MyRequestCache())
                .and()
                .authorizeRequests()
                .antMatchers("/auth/login","/token/logout").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                //允许iframe调用
                .headers().frameOptions().disable();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable() // 使用jwt，可以允许跨域
//                .authorizeRequests()
//                .antMatchers("/oauth/token", "/oauth/authorize", "/auth/login",
//                        "/authentication/form", "/oauth/form", "/oauth/check_token").permitAll()
//                .antMatchers("/favicon-20190918085337900.ico", "/js/jquery-3.4.1.min.js").permitAll()
//                .antMatchers("/**").authenticated()
//                //.anyRequest().authenticated()
//                // 所有请求都要认证
//                .and().httpBasic();// http_basic方式进行认证
//
//        http.formLogin().loginPage("/auth/login").loginProcessingUrl("/oauth/form");
//    }
}

