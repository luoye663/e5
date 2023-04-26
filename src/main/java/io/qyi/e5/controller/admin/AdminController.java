package io.qyi.e5.controller.admin;

import io.qyi.e5.bean.result.Result;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.ExecutorPoolService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.EncryptUtil;
import io.qyi.e5.util.ResultUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Resource
    ExecutorPoolService poolService;

    /**
     * 测试队列
     *
     * @Author: 落叶随风
     * @Date: 2020/9/7  14:44
     * @Return: * @return: void
     */
    @GetMapping("/send")
    public void send(@RequestParam int githubId, @RequestParam int outlookId) {
        Task.updateOutlookExecDateTime(githubId, outlookId);
    }

    @GetMapping("/execute")
    public void execute(@RequestParam int githubId, @RequestParam int outlookId) {
        Task.executeE5(githubId, outlookId);
    }


    /**
     * 清空redis
     *
     * @Author: 落叶随风
     * @Date: 2020/9/7  14:41
     * @Return: * @return: java.lang.String
     */
    @GetMapping("/emptyRedis")
    public String emptyRedis() {
        redisUtil.deleteALL();
        return "ok";
    }

    /**
     * 设置公告
     * @param text:
     * @Author: 落叶随风
     * @Date: 2021/7/26  15:30
     * @Return: * @return: java.lang.String
     */
    @RequestMapping("setAnnouncement")
    public String setAnnouncement(String text) throws IOException {
        File file = ResourceUtils.getFile("classpath:announcement.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(text);
        writer.close();
        return "ok";
    }

    /**
     * 通过配置的密码获取管理员token
     * @param passwd:
     * @Author: 落叶随风
     * @Date: 2021/7/26  15:29
     * @Return: * @return: java.lang.String
     */
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


    /**
     * 查询线程池状态
     * @return
     */
    @GetMapping("/findPoolStatus")
    public Result findPoolStatus() {
        ExecutorService threadPool = poolService.getThreadPool();

        int activeCount = ((ThreadPoolExecutor) threadPool).getActiveCount();

        long completeTaskCount = ((ThreadPoolExecutor) threadPool).getCompletedTaskCount();

        int poolSize = ((ThreadPoolExecutor) threadPool).getPoolSize();

        long taskCount = ((ThreadPoolExecutor) threadPool).getTaskCount();

        long largestPoolSize = ((ThreadPoolExecutor) threadPool).getLargestPoolSize();

        long maximumPoolSize = ((ThreadPoolExecutor) threadPool).getMaximumPoolSize();

        Map<String, Object> map = new HashMap<>();
        map.put("活动线程数", activeCount);
        map.put("执行完成的任务数", completeTaskCount);
        map.put("核心线程数", poolSize);
        map.put("线程池中的任务总量", taskCount);
        map.put("过去执行过的最多的任务数", largestPoolSize);
        map.put("线程池最大线程数", maximumPoolSize);

        return ResultUtil.success(map);

    }

}
