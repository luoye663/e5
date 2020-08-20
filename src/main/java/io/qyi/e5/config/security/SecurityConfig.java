package io.qyi.e5.config.security;

import io.qyi.e5.config.security.filter.LinkTokenAuthenticationFilter;
import io.qyi.e5.config.security.filter.LoginAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityAuthenticationHandler securityAuthenticationHandler;
    @Autowired
    UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
    @Autowired
    UrlAccessDecisionManager myAccessDecisionManager;
    @Autowired
    UrlInvocationSecurityMetadataSourceService myInvocationSecurityMetadataSourceService;

    @Value("${web.static.filtrate}")
    String[] webFiltrate;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("AuthenticationManagerBuilder auth");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        super.configure(web);
        /*放行静态资源*/
        web.ignoring().antMatchers(webFiltrate);
    }

    //  通过重载该方法，可配置如何通过拦截器保护请求。
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("HttpSecurity http");
        /*自定义*/
        http.csrf().disable();
        LoginAuthenticationFilter authenticationFilter = new LoginAuthenticationFilter();

        log.info("自定义用户认证处理逻辑");
//        自定义用户认证处理逻辑时，需要指定AuthenticationManager，否则无法认证
        authenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

//      指定自定义的认证成功和失败的处理器
        authenticationFilter.setAuthenticationSuccessHandler(securityAuthenticationHandler);
        authenticationFilter.setAuthenticationFailureHandler(securityAuthenticationHandler);

//        把自定义的用户名密码认证过滤器和处理器添加到UsernamePasswordAuthenticationFilter过滤器之前
        http.authenticationProvider(usernamePasswordAuthenticationProvider).addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests().anyRequest().authenticated().withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor());
        http.addFilterBefore(new LinkTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        /*关闭创建session*/
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
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
