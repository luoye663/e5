package io.qyi.e5.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

@Slf4j
@Component
public class ExecutorPoolService {

    @Value("${e5.system.threadPool}")
    Integer poolSize;

    @Value("${e5.system.maximumPoolSize}")
    int maximumPoolSize;
    @Value("${e5.system.blockingQueueSize}")
    int blockingQueueSize;


    private ExecutorService threadPool;

    @PostConstruct
    public void init() {
        threadPool = new ThreadPoolExecutor(
                //要保留在池中的线程数，即使它们处于空闲状态，除非设置了 allowCoreThreadTimeOut
                poolSize,
                //池中允许的最大线程数
                maximumPoolSize,
                //当线程池中空闲线程数量超过corePoolSize时，多余的线程会在多长时间内被销毁；当线程数大于核心时，这是多余的空闲线程在终止之前等待新任务的最长时间。
                10,
                //unit:keepAliveTime的单位
                TimeUnit.SECONDS,
                //任务队列，被添加到线程池中，但尚未被执行的任务；它一般分为直接提交队列、有界任务队列、无界任务队列、优先任务队列几种；
                new LinkedBlockingQueue<>(blockingQueueSize), // 有界队列
                //线程工厂，用于创建线程，一般用默认即可； new CustThreadFactory(),
                Executors.defaultThreadFactory(),
                //拒绝策略；当任务太多来不及处理时，如何拒绝任务；
                new CustRejectedExecutionHandler()
        );

    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /*拒绝策略*/
    class CustRejectedExecutionHandler implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                // e.printStackTrace();
                log.error("队列拒绝策略错误，问题:  {}", e.getMessage());
            }
        }

    }


}
