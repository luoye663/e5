package io.qyi.e5.config.security.filter;

import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-28 11:56
 **/
@Slf4j
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    protected LoginAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    protected LoginAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    public LoginAuthenticationFilter() {
        super(new AntPathRequestMatcher("/auth2/receive", "GET"));
        log.info("注册 LoginAuthenticationFilter");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        /*if (!httpServletRequest.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("不支持该验证方法: " + httpServletRequest.getMethod());
        } else {

        }*/
            /**
             * 从http请求中获取用户输入的用户名和密码信息
             * 这里接收的是form形式的参数，如果要接收json形式的参数，修改这里即可
             */
            String code = httpServletRequest.getParameter("code");
            String state = httpServletRequest.getParameter("state");
            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state)) {
                throw new UsernameNotFoundException("CustomUsernamePasswordAuthenticationFilter获取用户认证信息失败");
            }
            /**
             * 使用用户输入的用户名和密码信息创建一个未认证的用户认证Token
             */
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(code, state);
//            设置身份认证的详情信息
            this.setDetails(httpServletRequest, authRequest);

//            通过AuthenticationManager调用相应的AuthenticationProvider进行用户认证
            return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 设置身份认证的详情信息
     */
    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
