package io.qyi.e5.controller.auth2;

import io.qyi.e5.bean.result.Result;
import io.qyi.e5.github.mapper.GithubMapper;
import io.qyi.e5.github.service.IGithubService;
import io.qyi.e5.user.mapper.UserMapper;
import io.qyi.e5.util.EncryptUtil;
import io.qyi.e5.util.ResultUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-19 00:23
 **/
@RequestMapping("/auth2")
@RestController
public class Auth {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IGithubService githubService;

    @Value("${redis.auth2.github}")
    String states;

    @Value("${github.client_id}")
    String client_id;


    @RequestMapping("/getGithubUrl")
    public Result getGithubUrl() {
        String state = EncryptUtil.getInstance().SHA1Hex(UUID.randomUUID().toString());
        redisUtil.set(states + state, true, 600);
        return ResultUtil.success("https://github.com/login/oauth/authorize?client_id=" + client_id + "&redirect_uri=https://e5.qyi.io/auth2/receive&state=" + state);
    }

   /* @RequestMapping("/receive")
    public Result Receive(String code, String state, HttpServletResponse response) throws Exception {
        System.out.println(code);
        System.out.println(state);
        if (!redisUtil.hasKey(states + state)) {
            return ResultUtil.error(ResultEnum.STATE_HAS_EXPIRED);
        }
        redisUtil.del(states + state);
        String accessToken = githubService.getAccessToken(code);
        if (accessToken != null) {
            UserInfo userInfo = githubService.getUserInfo(accessToken);
            if (userInfo != null) {
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
                return ResultUtil.success();
            }
        }
        return ResultUtil.error(ResultEnum.INVALID_EMAIL);
    }*/

}
