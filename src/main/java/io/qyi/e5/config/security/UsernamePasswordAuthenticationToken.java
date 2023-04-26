package io.qyi.e5.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-28 15:23
 * setAuthenticated()：判断是否已认证 生成登录session，同用户不用再校验
 **/
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * token
     */
    private String code;

    private String state;

    private String name;

    private String avatar_url;

    private String Token;

    private String Authority;

    private int github_id;


    //    创建未认证的用户名密码认证对象
    public UsernamePasswordAuthenticationToken() {
        super(null);
    }

    //    创建未认证的用户名密码认证对象
    public UsernamePasswordAuthenticationToken(String code) {
        super(null);
        this.code = code;
        super.setAuthenticated(false);
        logger.debug("创建未认证的用户名密码认证对象1 setAuthenticated ->false loading ...");
    }

    //    创建未认证的用户名密码认证对象
    public UsernamePasswordAuthenticationToken(String code, String state) {
        super(null);
        this.code = code;
        this.state = state;
        super.setAuthenticated(false);
        logger.debug("创建未认证的用户名密码认证对象2 setAuthenticated ->false loading ...");
    }


    //  创建已认证的用户密码认证对象
    public UsernamePasswordAuthenticationToken(String name, String avatar_url, int github_id, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.name = name;
        this.avatar_url = avatar_url;
        this.github_id = github_id;
        super.setAuthenticated(true);
    }

    //  创建已认证的用户密码认证对象
    public UsernamePasswordAuthenticationToken(String name, String avatar_url, int github_id, String token, String Authority, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.name = name;
        this.avatar_url = avatar_url;
        this.github_id = github_id;
        this.Token = token;
        this.Authority = Authority;
        super.setAuthenticated(true);
    }


    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    @Override
    public String getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return this.code;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public int getGithub_id() {
        return github_id;
    }

    public void setGithub_id(int github_id) {
        this.github_id = github_id;
    }

    public String getAuthority() {
        return Authority;
    }

    public void setAuthority(String authority) {
        Authority = authority;
    }
}
