package io.qyi.e5.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

/**
 * 决策管理器
 *
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-06-15 16:11
 **/
@Slf4j
@Service
public class UrlAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        log.debug("进入权限判断!");
        if (collection == null) {
            return;
        }
        log.debug("object is a URL. {}", o.toString());
        //所请求的资源拥有的权限(一个资源对多个权限)
        Iterator<ConfigAttribute> iterator = collection.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            //访问所请求资源所需要的权限
            String needPermission = configAttribute.getAttribute();
            log.debug("访问 " + o.toString() + " 需要的权限是：" + needPermission);
            if (needPermission == null) {
                break;
            }
            //用户所拥有的权限authentication
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority ga : authorities) {
                if (needPermission.equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        //没有权限
        throw new AccessDeniedException("无权限!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        log.info("进入权限判断! ConfigAttribute configAttribute");
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        log.info("进入权限判断! Class<?> aClass");
        return true;
    }
}
