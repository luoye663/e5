package io.qyi.e5.config.security;

import io.qyi.e5.config.security.filter.LinkTokenAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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

    @Autowired
    GithubAuth2AuthenticationConfig githubAuth2AuthenticationConfig;


    @Value("${web.static.filtrate}")
    String[] webFiltrate;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("AuthenticationManagerBuilder auth");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        super.configure(web);
        /*放行静态资源,这里放行不会去执行 AbstractAuthenticationProcessingFilter */
        web.ignoring().antMatchers(webFiltrate);

    }

    //  通过重载该方法，可配置如何通过拦截器保护请求。
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("HttpSecurity http");

        /*自定义*/
        log.info("注册gituhb授权登录");
//        http.authorizeRequests().antMatchers("/user/login", "/user/loginFrom", "/auth2/getGithubUrl").permitAll()// 指定相应的请求 不需要验证
////                .and()
////                .authorizeRequests().antMatchers("/quartz/**").permitAll()//测试
//                .anyRequest()// 任何请求
//                .authenticated();// 都需要身份认证

        /*验证token*/
        http.addFilterBefore(new LinkTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.csrf().disable().apply(githubAuth2AuthenticationConfig);
        /*添加自定义权限管理器*/
        http.authorizeRequests().anyRequest().authenticated().withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor());
        /*关闭创建session*/
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * 自定义 FilterSecurityInterceptor  ObjectPostProcessor 以替换默认配置达到动态权限的目的
     *
     * @return ObjectPostProcessor
     */
    private ObjectPostProcessor<FilterSecurityInterceptor> filterSecurityInterceptorObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                log.info("加载自定义url权限");
                object.setAccessDecisionManager(myAccessDecisionManager);
                object.setSecurityMetadataSource(myInvocationSecurityMetadataSourceService);
                return object;
            }
        };
    }

}
