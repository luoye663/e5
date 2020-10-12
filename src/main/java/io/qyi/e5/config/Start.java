package io.qyi.e5.config;

import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-10-12 16:58
 **/
@Component
@Slf4j
public class Start {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ITask Task;

    @PostConstruct
    public void initRedis() {
        log.info("清空redis...... ");
        redisUtil.delAll();
       /* log.info("重新添加队列...... ");
        Task.sendTaskOutlookMQALL();*/

    }
}
