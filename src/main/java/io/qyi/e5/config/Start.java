package io.qyi.e5.config;

import io.qyi.e5.outlook.bean.OutlookMq;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.ExecutorPoolService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;

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

    @Value("${isdebug:true}")
    private boolean isdebug;

    @Resource
    ExecutorPoolService poolService;


    @Scheduled(cron = "0 0/1 * * * ? ")
    private void distributeTask() {
        if (isdebug) {
            log.debug("Debug模式，跳过执行");
//            System.out.println("!!!!!!!!!!!!!!!!!!!!");
//            return;
        }
        ExecutorService threadPool = poolService.getThreadPool();

        List<Outlook> runOutlookList = outlookService.findRunOutlookList();
        CountDownLatch cdl = new CountDownLatch(runOutlookList.size());

        log.info("查询到待调用的数量: {}", runOutlookList.size());



        runOutlookList.forEach(outlook -> {
            // threadPool.execute(new task(outlook,cdl));
            threadPool.submit(new task(outlook, cdl));
        });

        /*等待线程池内的线程执行完毕*/
        try {
            cdl.await();
        } catch (InterruptedException e) {
            // e.printStackTrace();
            log.error("等待线程池任务出错，消息: {}",e.getMessage());
        }
        log.debug("本轮调用完成!");
    }

    /*任务执行*/
    class task implements Runnable {
        Outlook value;
        CountDownLatch cdl;

        public task(Outlook outlook, CountDownLatch cdl) {
            value = outlook;
            this.cdl = cdl;
        }

        @Override
        public void run() {
            try {
                Task.listen(new OutlookMq(value.getGithubId(), value.getId()));
            } finally {
                this.cdl.countDown();
            }
        }
    }




}
