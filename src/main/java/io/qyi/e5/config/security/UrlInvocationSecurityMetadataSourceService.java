package io.qyi.e5.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-06-17 16:25
 **/
@Slf4j
@Service
public class UrlInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    private HashMap<String, Collection<ConfigAttribute>> map =null;
    /**
     * 加载权限表中所有权限
     * 这里有一个坑，如果map返回是null，是不会AccessDecisionManager，默认放行。
     */
    public void loadResourceDefine(){
        log.info("加载权限表中所有权限");
        map = new HashMap<>();
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        Map<String, String> permissions = new HashMap<>();
        /*这里只是简单的配置*/
        permissions.put("/admin/**", "admin");
        permissions.put("/**", "user");
        permissions.put("/auth2/**", "ROLE_ANONYMOUS");
        permissions.put("/error", "ROLE_ANONYMOUS");

        Iterator<Map.Entry<String, String>> iterator = permissions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();

            array = new ArrayList<>();
            cfg = new SecurityConfig(value);
            array.add(cfg);
            map.put(key, array);
        }

    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if(map ==null) loadResourceDefine();
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        AntPathRequestMatcher matcher;
        String resUrl;
        for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                return map.get(resUrl);
            }
        }
        return null;
    }


    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
