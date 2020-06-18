package io.qyi.e5.config.security;

import io.qyi.e5.config.security.filter.LinkTokenAuthenticationFilter;
import io.qyi.e5.config.security.filter.LoginAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-28 16:24
 **/
@Component
public class UsernamePasswordAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    @Autowired
    SecurityAuthenticationHandler securityAuthenticationHandler;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        LoginAuthenticationFilter authenticationFilter = new LoginAuthenticationFilter();


        logger.info("自定义用户认证处理逻辑");
//        自定义用户认证处理逻辑时，需要指定AuthenticationManager，否则无法认证
        authenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

//      指定自定义的认证成功和失败的处理器
        authenticationFilter.setAuthenticationSuccessHandler(securityAuthenticationHandler);
        authenticationFilter.setAuthenticationFailureHandler(securityAuthenticationHandler);
//        把自定义的用户名密码认证过滤器和处理器添加到UsernamePasswordAuthenticationFilter过滤器之前
        http.authenticationProvider(usernamePasswordAuthenticationProvider)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


    }

}
