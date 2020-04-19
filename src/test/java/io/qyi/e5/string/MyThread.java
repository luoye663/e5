package io.qyi.e5.string;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-20 00:38
 **/
public class MyThread implements Runnable {
    private Random random = null;

    public MyThread() {
        random = new Random();
    }

    @Override
    public void run() {
        try {
            System.out.println("任务执行开始:" + new Date());
            /**使用随机延时[0-3]秒来模拟执行任务*/
            int sleepNumber = random.nextInt(3);
            TimeUnit.SECONDS.sleep(2);
            System.out.println("任务执行完毕:" + new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(2);
        System.out.println("开始任务");

        for (int i = 0; i < 4; i++) {
            //延时3秒执行
            service.schedule(new MyThread(), 3, TimeUnit.SECONDS);
        }
        System.out.println("结束任务");
    }
}
