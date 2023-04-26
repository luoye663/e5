package io.qyi.e5.util;


import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 获取当前登录用户
     * @return
     */
    public static String getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("当前无用户登录");
        } else {
            return authentication.getName();
        }
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    public static UsernamePasswordAuthenticationToken getUserInfo() {
        UsernamePasswordAuthenticationToken details = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (details == null) {
            throw new RuntimeException("当前无用户登录");
        }
        return details;

    }
}
