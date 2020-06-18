package io.qyi.e5.config.security;

import io.qyi.e5.config.security.filter.LinkTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2019-12-26 14:15
 **/
@Configuration
@EnableWebSecurity //开启wen安全功能
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityAuthenticationHandler securityAuthenticationHandler;


    @Autowired
    UsernamePasswordAuthenticationConfig usernamePasswordAuthenticationConfig;

    @Autowired
    MyAccessDecisionManager myAccessDecisionManager;

    @Autowired
    MyInvocationSecurityMetadataSourceService myInvocationSecurityMetadataSourceService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("AuthenticationManagerBuilder auth");
    }

    //  通过重载该方法，可配置如何通过拦截器保护请求。
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("HttpSecurity http");
       /* http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setSecurityMetadataSource(myInvocationSecurityMetadataSourceService);
                        o.setAccessDecisionManager(myAccessDecisionManager);
                        return o;
                    }
                });*/
        /*关闭创建session*/
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.authorizeRequests().antMatchers("/user/login", "/user/loginFrom", "/auth2/getGithubUrl").permitAll()// 指定相应的请求 不需要验证
//                .accessDecisionManager(myAccessDecisionManager)
        http.authorizeRequests().anyRequest().authenticated().withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor());
        http.addFilterBefore(new LinkTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        /*自定义*/
        http.csrf().disable().apply(usernamePasswordAuthenticationConfig);
        //自定义过滤器
    }
    /**
     * 自定义 FilterSecurityInterceptor  ObjectPostProcessor 以替换默认配置达到动态权限的目的
     * @return ObjectPostProcessor
     */
    private ObjectPostProcessor<FilterSecurityInterceptor> filterSecurityInterceptorObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                object.setAccessDecisionManager(myAccessDecisionManager);
                object.setSecurityMetadataSource(myInvocationSecurityMetadataSourceService);
                return object;
            }
        };
    }

}
