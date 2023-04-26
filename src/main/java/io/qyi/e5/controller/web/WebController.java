package io.qyi.e5.controller.web;

import io.qyi.e5.bean.result.Result;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.github.service.IGithubService;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import io.qyi.e5.util.ResultUtil;
import io.qyi.e5.util.SecurityUtils;
import io.qyi.e5.util.StringUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-27 16:03
 **/
@RestController
public class WebController {

    @Autowired
    IOutlookService outlookService;

    @Autowired
    IGithubService GithubService;

    @Autowired
    IOutlookLogService iOutlookLogService;

    @Autowired
    RedisUtil redisUtil;

    @Value("${outlook.replyUrl}")
    String replyUrl;

    @Value("${outlook.replyUrlDebug}")
    String replyUrlDebug;

    @Value("${outlook.authorize.url}")
    String authorizeUrl;

    @Value("${isdebug}")
    boolean isDebug;

    @RequestMapping("/")
    public Result index() {
        return ResultUtil.error(-1, "This is api server!");
    }

    /**
     *
     * 删除用户信息
    * @Description:
    * @param: * @param
    * @return: io.qyi.e5.bean.result.Result
    * @Author: 落叶随风
    * @Date: 2020/4/17
    */
    @GetMapping("/user/delete")
    public Result delete(){
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        /*删除数据库信息*/
        // int outlooklog = iOutlookLogService.deleteInfo(authentication.getGithub_id());
        int outlook = outlookService.deleteInfo(authentication.getGithub_id());
        int github = GithubService.deleteInfo(authentication.getGithub_id());
        /*删除redis中信息*/
        String token = authentication.getToken();
        if (redisUtil.hasKey("token:" + token)) {
            redisUtil.del("token:" + token);
        }
        /*返回结果信息*/
        Map<String, Integer> map = new HashMap<>();
        // map.put("outlooklog", outlooklog);
        map.put("outlook", outlook);
        map.put("github", github);
        return ResultUtil.success(map);

    }

    @RequestMapping("getAnnouncement")
    public String getAnnouncement() throws IOException {
        String s = StringUtil.readTxt(ResourceUtils.getFile("classpath:announcement.txt"));
        return s;
    }

    @RequestMapping("getUserReplyUrlToOutlook")
    public Result getUserReplyUrlToOutlook() {
        String userIdMd5 = DigestUtils.md5DigestAsHex(String.valueOf(SecurityUtils.getUserInfo().getGithub_id()).getBytes());

        String reUrl;
        if (isDebug) {
            reUrl = String.format(replyUrlDebug, userIdMd5);
        } else {
            reUrl = String.format(replyUrl, userIdMd5);
        }
        return ResultUtil.success(reUrl);
    }

}
