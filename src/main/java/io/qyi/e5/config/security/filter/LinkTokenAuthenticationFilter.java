package io.qyi.e5.config.security.filter;

import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.util.SpringUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Token校验
 *
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-05 00:42
 **/
public class LinkTokenAuthenticationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = httpServletRequest.getHeader("token");
        if (token != null) {
            RedisUtil RedisUtil = SpringUtil.getBean(RedisUtil.class);
            if (RedisUtil.hasKey("token:" + token)) {
                Map<Object, Object> userInfo = RedisUtil.hmget("token:" + token);
                //        将未认证的Authentication转换成自定义的用户认证Token
                UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(userInfo.get("github_name") == null ? "" : userInfo.get("github_name").toString(),
                        userInfo.get("avatar_url").toString(), (int) userInfo.get("github_id"), userInfo.get("authority").toString(), AuthorityUtils.createAuthorityList("user"));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken1);
                System.out.println("完成授权,角色:" + userInfo.get("authority").toString());
            }
        }
        System.out.println("--------------Token鉴权---------------");
        /*设置跨域*/
        HttpServletResponse response = httpServletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
        /*如果是OPTIONS则结束请求*/
        if (HttpMethod.OPTIONS.toString().equals(httpServletRequest.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } else {
            filterChain.doFilter(httpServletRequest, response);
        }

    }
}
