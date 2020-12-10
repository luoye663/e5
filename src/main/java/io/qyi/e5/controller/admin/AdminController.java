package io.qyi.e5.controller.admin;

import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.EncryptUtil;
import io.qyi.e5.util.StringUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @program: 此类里大多都是测试
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-16 01:01
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    IOutlookService outlookService;

    @Autowired
    ITask Task;

    @Autowired
    RedisUtil redisUtil;

    @Value("user.admin.githubId")
    String adminGithubId;

    @Value("${user.admin.debug.passwd}")
    String userAdminDebugPasswd;

    @Value("${redis.user.token}")
    String token_;

    @Value("${user.token.expire}")
    private int tokenExpire;

    /**
     * 测试队列
     * @Author: 落叶随风
     * @Date: 2020/9/7  14:44
     * @Return: * @return: void
     */
    @GetMapping("/send")
    public void send() {
        Task.sendTaskOutlookMQ(Integer.valueOf(adminGithubId) );
    }

    /**
     * 对所有队列重新添加
     * @Author: 落叶随风
     * @Date: 2020/9/7  14:43
     * @Return: * @return: java.lang.String
     */
    @GetMapping("/sendAll")
    public String sendAll() {
        Task.sendTaskOutlookMQALL();
        return "ok";
    }

    /**
     * 清空redis
     * @Author: 落叶随风
     * @Date: 2020/9/7  14:41
     * @Return: * @return: java.lang.String
     */
    @GetMapping("/emptyRedis")
    public String emptyRedis() {
        redisUtil.deleteALL();
        return "ok";
    }

    @RequestMapping("setAnnouncement")
    public String setAnnouncement(String text) throws IOException {
        File file = ResourceUtils.getFile("classpath:announcement.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(text);
        writer.close();
        return "ok";
    }

    @RequestMapping("getDebugAdminToken")
    public String getDebugAdminToken(String passwd) {
        if (userAdminDebugPasswd.equals(passwd)) {
            String token = EncryptUtil.getInstance().SHA1Hex(UUID.randomUUID().toString());
            /*配置角色,这里只是简单的配置，实际上需要从数据库中读取角色*/
            List<String> list_Authority = new ArrayList<>();
            list_Authority.add("user");
            list_Authority.add("admin");
            String[] Authority = list_Authority.toArray(new String[list_Authority.size()]);
            /*写token信息到redis*/
            Map<String, Object> userInfo_redis = new HashMap<>();
            userInfo_redis.put("github_name", "admin");
            userInfo_redis.put("github_id", 10000);
            userInfo_redis.put("avatar_url", "https://www.baidu.com");
            userInfo_redis.put("authority", list_Authority);
            redisUtil.hmset(token_ + token, userInfo_redis, tokenExpire);
//       创建一个已认证的token
//            UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(userInfo_redis.get("github_name").toString(),
//                    userInfo_redis.get("github_name").toString(), (int)userInfo_redis.get("github_name"), token, "user", AuthorityUtils.createAuthorityList(Authority));
            return token;
        }
        return "la la la";
    }

}
