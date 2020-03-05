package io.qyi.e5.test;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-02 16:37
 **/
public class quartzDome01 {

    @Test
    public void d0() throws Exception {
        try {
            demo01();
        } catch (SchedulerException e) {
            System.err.println("发现任务已经在数据库存在了，直接从数据库里运行:"+ e.getMessage());
            // TODO Auto-generated catch block
            resumeJobFromDatabase();
        }

    }

    private void resumeJobFromDatabase() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        // 等待200秒，让前面的任务都执行完了之后，再关闭调度器
        Thread.sleep(200000);
        scheduler.shutdown(true);
    }

    public void demo01() throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        JobDetail jobDetail = JobBuilder.newJob(RamJob.class)
                .withDescription("this is a job")
                .withIdentity("job1", "group1")
                .usingJobData("level", "老")
                .build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("job","司机");

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .startNow()
//                .withDescription("this is a trigger1")
//                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(3))
                .withIdentity("mailjob1", "mailgroup") //定义任务名称和分组
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
        //增加Job监听
        MailJobListener mailJobListener = new MailJobListener();
        KeyMatcher<JobKey> uKeyMatcher = KeyMatcher.keyEquals(jobDetail.getKey());
        scheduler.getListenerManager().addJobListener(mailJobListener,uKeyMatcher);


        //将触发器以及调度任务详情绑定到调度器上
        scheduler.scheduleJob(jobDetail,trigger);
        //启动调度器
        scheduler.start();

    }
}
