package io.qyi.e5.config.security;

import io.qyi.e5.config.security.filter.LinkTokenAuthenticationFilter;
import io.qyi.e5.service.security.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private SecurityUserService securityUserService;

    @Autowired
    UsernamePasswordAuthenticationConfig usernamePasswordAuthenticationConfig;



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("AuthenticationManagerBuilder auth");
//        auth.userDetailsService(securityUserService).passwordEncoder(new BCryptPasswordEncoder());
//        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
//                .withUser("user").password(new BCryptPasswordEncoder().encode("123")).roles("user").and()
//                .withUser("admin").password(new BCryptPasswordEncoder().encode("admin")).roles("USER", "ADMIN");
    }

    //  通过重载该方法，可配置如何通过拦截器保护请求。
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("HttpSecurity http");
        /*http.authorizeRequests().antMatchers("/").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.logout().permitAll();
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login1")
                .successHandler(securityAuthenticationHandler)
                .failureHandler(securityAuthenticationHandler)
//                .loginProcessingUrl("api/getInfo")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("logout")
                .logoutSuccessHandler( securityAuthenticationHandler);*/
        http.addFilterBefore(new LinkTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.csrf().disable()
                .apply(usernamePasswordAuthenticationConfig);
        /*关闭创建session*/
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin()
                .loginPage("/user/login")// 登陆页面
                .loginProcessingUrl("/user/loginFrom")// 登陆表单提交请求
                .and()
                .authorizeRequests().antMatchers("/user/login", "/user/loginFrom", "/auth2/getGithubUrl").permitAll()// 指定相应的请求 不需要验证
//                .and()
//                .authorizeRequests().antMatchers("/quartz/**").permitAll()//测试
                .anyRequest()// 任何请求
                .authenticated();// 都需要身份认证
//        http.exceptionHandling().accessDeniedHandler();
//        http.formLogin().loginProcessingUrl("api/getInfo");

//        http.formLogin().usernameParameter("username");
//        http.formLogin().passwordParameter("password");

    }

    /*@Bean
    public LinkTokenAuthenticationFilter linkTokenAuthenticationFilter (){
        return new LinkTokenAuthenticationFilter();
    }*/

    /*@Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return new RestAuthenticationAccessDeniedHandler();
    }*/

   /* @Override
    public void configure(WebSecurity web) {
        System.out.println("WebSecurity web");
        String antPatterns = "/pdfjs-2.1.266/**,/favicon.ico,/css/**,/js/**,/ico/**,/images/**,/jquery-1.12.4/**,/uuid-1.4/**,/layui-2.4.5/**,/jquery-easyui-1.6.11/**,/zTree-3.5.33/**,/select2-4.0.5/**,/greensock-js-1.20.5/**";
        web.ignoring().antMatchers(antPatterns.split(","));
    }*/

}
