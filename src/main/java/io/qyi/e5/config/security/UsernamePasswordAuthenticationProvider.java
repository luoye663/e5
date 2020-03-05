package io.qyi.e5.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.bean.result.ResultEnum;
import io.qyi.e5.github.entity.Github;
import io.qyi.e5.github.entity.UserInfo;
import io.qyi.e5.github.mapper.GithubMapper;
import io.qyi.e5.service.github.GithubService;
import io.qyi.e5.util.ResultUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-28 15:48
 **/
//自定义的用户名密码认证实现类
@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${redis.auth2.github}")
    String states;

    @Value("${isdebug}")
    boolean isDebug;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    GithubMapper githubMapper;

    @Autowired
    GithubService githubService;

    //   验证
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        将未认证的Authentication转换成自定义的用户认证Token
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
//        根据用户Token中的用户名查找用户信息，如果有该用户信息，则验证用户密码是否正确
        String code = authenticationToken.getCode();
        String state = authenticationToken.getState();
        logger.info("Github 认证: code：{} state：{} Token：", code, state);
        if (isDebug) {
            UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken("debugName",
                    "DebugAvatar",19658189, AuthorityUtils.createAuthorityList("user"));
            authenticationToken1.setDetails(authenticationToken);
            return authenticationToken1;
        }
        if (!redisUtil.hasKey(states + state)) {
            throw new UsernameNotFoundException("status不存在");
//            return ResultUtil.error(ResultEnum.STATE_HAS_EXPIRED);
        }
        redisUtil.del(states + state);
        String accessToken = githubService.getAccessToken(code);
        if (accessToken == null) {
            logger.error("accessToken 为空!");
            throw new BadCredentialsException("accessToken 为空!");
        }
        UserInfo userInfo = githubService.getUserInfo(accessToken);
        if (userInfo == null) {
            logger.error("获取github用户信息失败!");
            throw new BadCredentialsException("获取github用户信息失败!");
        }
        QueryWrapper<Github> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("github_id", userInfo.getGithub_id());
        Github github = githubMapper.selectOne(queryWrapper);
//                未注册就进行注册
        if (github == null) {
            github = new Github();
            github.setAccessToken(accessToken)
                    .setAvatarUrl(userInfo.getAvatar_url())
                    .setGithubId(userInfo.getGithub_id())
                    .setName(userInfo.getName())
                    .setLogin(userInfo.getLogin());
            githubMapper.insert(github);
        } else {
//                    已注册就进行更新 AccessToken
            github.setAccessToken(accessToken);
            githubMapper.update(github, queryWrapper);
        }


//       创建一个已认证的token
        UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(github.getName(),
                github.getAvatarUrl(),github.getGithubId(), AuthorityUtils.createAuthorityList("user"));

//      设置一些详细信息
        authenticationToken1.setDetails(authenticationToken);

        return authenticationToken1;
    }

    @Override
    public boolean supports(Class<?> authentication) {
//        指定该认证处理器能对 UsernamePasswordAuthenticationToken 对象进行认证
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
