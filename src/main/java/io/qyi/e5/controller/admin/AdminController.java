package io.qyi.e5.controller.admin;

import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}
