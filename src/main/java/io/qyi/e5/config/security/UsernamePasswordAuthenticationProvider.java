package io.qyi.e5.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.github.entity.Github;
import io.qyi.e5.github.entity.UserInfo;
import io.qyi.e5.github.service.IGithubService;
import io.qyi.e5.util.EncryptUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Value("${redis.user.token}")
    String token_;

    @Value("${isdebug}")
    boolean isDebug;

    @Value("${user.admin.githubId}")
    int adminGithubId;

    @Value("${user.token.expire}")
    private int tokenExpire;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IGithubService githubService;

    //   验证
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        将未认证的Authentication转换成自定义的用户认证Token
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
//        根据用户Token中的用户名查找用户信息，如果有该用户信息，则验证用户密码是否正确
        String code = authenticationToken.getCode();
        String state = authenticationToken.getState();
        logger.info("Github 认证: code：{} state：{} Token：", code, state);
        Map<String, Object> userInfo_redis = new HashMap<>();

        /*是否调试模式*/
        if (isDebug) {
            List<String> list = new ArrayList<>();
            list.add("admin");
            list.add("user");
            String[] l =list.toArray(new String[list.size()]);
            String token = EncryptUtil.getInstance().SHA1Hex(UUID.randomUUID().toString());
            UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken("debugName",
                    "DebugAvatar", adminGithubId, token, "admin", AuthorityUtils.createAuthorityList(l));
            authenticationToken1.setDetails(authenticationToken);
            userInfo_redis.put("github_name", "debug");
            userInfo_redis.put("github_id", adminGithubId);
            userInfo_redis.put("avatar_url", "https://www.baidu.com");
            userInfo_redis.put("authority", list);
            redisUtil.hmset(token_ + token, userInfo_redis, tokenExpire);
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
        Github github = githubService.selectOne(queryWrapper);
//      未注册就进行注册
        if (github == null) {
            github = new Github();
            github.setAccessToken(accessToken)
                    .setAvatarUrl(userInfo.getAvatar_url())
                    .setGithubId(userInfo.getGithub_id())
                    .setName(userInfo.getName())
                    .setLogin(userInfo.getLogin());
            githubService.insert(github);
        } else {
//          已注册就进行更新 AccessToken
            github.setAccessToken(accessToken);
            githubService.update(github, queryWrapper);
        }

        String token = EncryptUtil.getInstance().SHA1Hex(UUID.randomUUID().toString());
        /*配置角色,这里只是简单的配置，实际上需要从数据库中读取角色*/
        List<String> list = new ArrayList<>();
        list.add("user");
        if (adminGithubId == github.getGithubId()) {
            list.add("admin");
        }
        String[] Authority =list.toArray(new String[list.size()]);
        /*写token信息到redis*/
        userInfo_redis.put("github_name", github.getName());
        userInfo_redis.put("github_id", github.getGithubId());
        userInfo_redis.put("avatar_url", github.getAvatarUrl());
        userInfo_redis.put("authority", Authority);
        redisUtil.hmset(token_ + token, userInfo_redis, tokenExpire);


//       创建一个已认证的token
        UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(github.getName(),
                github.getAvatarUrl(), github.getGithubId() , AuthorityUtils.createAuthorityList(Authority));

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
