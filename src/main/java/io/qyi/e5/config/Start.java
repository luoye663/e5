package io.qyi.e5.config;

import io.qyi.e5.outlook.bean.OutlookMq;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
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
import java.time.LocalDateTime;
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

    @Value("${e5.system.threadPool}")
    private int poolSize = 10;

    private ExecutorService threadPool  = new ThreadPoolExecutor(
            //指定了线程池中的线程数量，它的数量决定了添加的任务是开辟新的线程去执行，还是放到workQueue任务队列中去；
            poolSize,
            //指定了线程池中的最大线程数量，这个参数会根据你使用的workQueue任务队列的类型，决定线程池会开辟的最大线程数量；
            poolSize,
            //当线程池中空闲线程数量超过corePoolSize时，多余的线程会在多长时间内被销毁；
            0,
            //unit:keepAliveTime的单位
            TimeUnit.MILLISECONDS,
            //任务队列，被添加到线程池中，但尚未被执行的任务；它一般分为直接提交队列、有界任务队列、无界任务队列、优先任务队列几种；
            new LinkedBlockingQueue<>(poolSize), // 有界队列
            //线程工厂，用于创建线程，一般用默认即可； new CustThreadFactory(),
            Executors.defaultThreadFactory(),
            //拒绝策略；当任务太多来不及处理时，如何拒绝任务；
            new CustRejectedExecutionHandler()
    );

    @PostConstruct
    public void init() {
        log.info("清空redis...... ");
        redisUtil.delAll();
       /* log.info("重新添加队列...... ");
        Task.sendTaskOutlookMQALL();*/

    }

    @Scheduled(cron = "0/10 * * * * ?")
    private void distributeTask() {
        List<Outlook> runOutlookList = outlookService.findRunOutlookList();
        CountDownLatch cdl = new CountDownLatch(runOutlookList.size());

        log.info("查询到待调用的数量: {}",runOutlookList.size());
        runOutlookList.forEach(outlook -> {
            threadPool.execute(new task(outlook,cdl));
        });
        /*等待线程池内的线程执行完毕*/
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*任务执行*/
    class task implements Runnable {
        Outlook value ;
        CountDownLatch cdl;
        public task(Outlook outlook,CountDownLatch cdl) {
            value = outlook;
            this.cdl = cdl;
        }
        @SneakyThrows
        @Override
        public void run() {
            System.out.println("消费数据: " + value);
            Task.listen(new OutlookMq(value.getGithubId(), value.getId()));
            this.cdl.countDown();
        }
    }


    /*拒绝策略*/
    class CustRejectedExecutionHandler implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
