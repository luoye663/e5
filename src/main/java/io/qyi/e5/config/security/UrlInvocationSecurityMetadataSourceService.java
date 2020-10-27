package io.qyi.e5.config.security;

import io.qyi.e5.config.security.bean.CollectionBean;
import io.qyi.e5.config.security.bean.dto.PermissionListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${web.security.admin}")
    private String[] securityAdmin;
    @Value("${web.security.user}")
    private String[] securityUser;
    @Value("${web.security.role_anonymous}")
    private String[] securitAnonymous;

    private List<CollectionBean> map = null;
    /**
     * 加载权限表中所有权限
     * 这里有一个坑，如果map返回是null，是不会AccessDecisionManager，默认放行。
     */
    public void loadResourceDefine(){
        log.info("加载权限表中所有权限");
        map = new ArrayList<>();
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        Map<String, String []> permissions = new HashMap<>();
        /*这里只是简单的配置*/
        List<PermissionListDto> permissionList = new ArrayList<>();
        Arrays.stream(securityAdmin).forEach(s -> permissionList.add(new PermissionListDto("admin",s)));
        Arrays.stream(securityUser).forEach(s -> permissionList.add(new PermissionListDto("user",s)));
        Arrays.stream(securitAnonymous).forEach(s -> permissionList.add(new PermissionListDto("ROLE_ANONYMOUS",s)));

        Iterator<PermissionListDto> iterator1 = permissionList.iterator();
        while (iterator1.hasNext()) {
            PermissionListDto next = iterator1.next();
            String role_name = next.getRoleName();
            String url = next.getUrl();

            array = new ArrayList<>();
            cfg = new SecurityConfig(role_name);
            array.add(cfg);
            /* url -> N x roleName*/
            CollectionBean collectionBean = new CollectionBean(url,array);
            map.add(collectionBean);
        }

    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if(map ==null) loadResourceDefine();
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        AntPathRequestMatcher matcher;
        String resUrl;
        Collection<ConfigAttribute> collection = new LinkedList<>();
        Iterator<CollectionBean> iterator1 = map.iterator();
        while (iterator1.hasNext()) {
            CollectionBean next = iterator1.next();
            resUrl = next.getUrl();
            matcher = new AntPathRequestMatcher(resUrl);
            if (matcher.matches(request)) {
                Iterator<ConfigAttribute> iterator = next.getConfigAttributes().iterator();
                while (iterator.hasNext()) {
                    collection.add(iterator.next());
                }
//                collection.add(map.get(resUrl))
//                return map.get(resUrl);
            }
        }
        if (collection.size() > 0) {
            return collection;
        }
        /*防止数据库中没有数据，不能进行权限拦截*/
        ConfigAttribute configAttribute = new SecurityConfig("ROLE_NO_USER");
        collection.add(configAttribute);
        return collection;
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
