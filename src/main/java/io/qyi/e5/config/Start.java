package io.qyi.e5.config;

import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-10-12 16:58
 **/
@Component
@Slf4j
@EnableScheduling
public class Start {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ITask Task;

    @Autowired
    IOutlookService outlookService;

    @PostConstruct
    public void initRedis() {
        log.info("清空redis...... ");
        redisUtil.delAll();
       /* log.info("重新添加队列...... ");
        Task.sendTaskOutlookMQALL();*/
    }

    @Scheduled(cron = "0/10 * * * * ?")
    private void distributeTask() {
        List<Outlook> runOutlookList = outlookService.findRunOutlookList();
        log.info("查询到待调用的数量: {}",runOutlookList.size());
        runOutlookList.forEach(outlook -> {
            Task.submit(outlook);
        });
        // Task.submit(runOutlookList.get(0));
    }

}
