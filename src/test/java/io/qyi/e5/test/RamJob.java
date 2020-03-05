package io.qyi.e5.test;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalTime;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-02 16:37
 **/
public class RamJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("启动定时任务......每十秒执行一次，共执行三次");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println(LocalTime.now().toString());
        System.out.println(jobDataMap.get("level") + "" + jobDataMap.get("job"));
    }
}
