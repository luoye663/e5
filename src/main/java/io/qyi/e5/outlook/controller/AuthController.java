package io.qyi.e5.outlook.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.bean.result.Result;
import io.qyi.e5.config.exception.APIException;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.EncryptUtil;
import io.qyi.e5.util.ResultUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-24 16:02
 **/
@RestController
@RequestMapping("/outlook/auth2")
public class AuthController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IOutlookService outlookService;

    @Value("${isdebug}")
    boolean isDebug;

    @Value("${redis.auth2.outlook}")
    String states;

    @Value("${outlook.replyUrl}")
    String replyUrl;

    @Value("${outlook.replyUrlDebug}")
    String replyUrlDebug;

    @Value("${outlook.authorize.url}")
    String authorizeUrl;

    @Autowired
    ITask Task;


    @RequestMapping("/receive")
    public Result Receive(String code, String state, String session_state) throws Exception {
        if (!redisUtil.hasKey(states + state)) {
            throw new APIException("state已过期，请到用户中心重新授权!");
        }
        /*这里不应该查询，在进行授权时因该把基础数据丢到redis*/
        QueryWrapper<Outlook> outlookQueryWrapper = new QueryWrapper<>();
        int outlookId = (int) redisUtil.get(states + state);
        outlookQueryWrapper.eq("id", outlookId);
        Outlook outlook = outlookService.getOne(outlookQueryWrapper);
        /*删除redis中的此键*/
        redisUtil.del(states + state);
        if (outlook == null) {
            throw new APIException("没有查询到此记录，请检查是否在系统中注册!");
        }
        String reUrl = "";
        if (isDebug) {
            reUrl = replyUrlDebug;
        } else {
            reUrl = replyUrl;
        }
        boolean authorization_code = outlookService.getTokenAndSave(code, outlook.getClientId(), outlook.getClientSecret(), reUrl
                , "authorization_code");
        if (!authorization_code) {
            throw new APIException("clientId 或 clientSecret 填写错误!授权失败!");
        }
        /*添加此用户进消息队列*/
        Task.updateOutlookExecDateTime(outlook.getGithubId(),outlookId);
        return ResultUtil.success();
    }

    @RequestMapping("/getAuthorizeUrl")
    public Result getAuthorizeUrl(int id) {
//       查询此用户的github_id与
        QueryWrapper<Outlook> outlookQueryWrapper = new QueryWrapper<>();
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        outlookQueryWrapper.eq("github_id", authentication.getGithub_id());
        outlookQueryWrapper.eq("id", id);
        Outlook outlook = outlookService.getOne(outlookQueryWrapper);

        if (outlook != null) {
            if (outlook.getClientId().length() < 1 || outlook.getClientSecret().length() < 1) {
                throw new APIException("没有设置key你授权啥呢!!!");
            }
            // 生成随机uuid标识用户
            String state = EncryptUtil.getInstance().SHA1Hex(UUID.randomUUID().toString());
            redisUtil.set(states + state, id, 600);
            String reUrl = "";
            if (isDebug) {
                reUrl = replyUrlDebug;
            } else {
                reUrl = replyUrl;
            }

            String url = String.format(authorizeUrl, outlook.getClientId(), reUrl, state);
            return ResultUtil.success(url);
        } else {
            throw new APIException("没有此记录");

        }

    }
}
