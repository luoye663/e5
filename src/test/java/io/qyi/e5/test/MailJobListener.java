package io.qyi.e5.test;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-03 09:39
 **/
public class MailJobListener implements JobListener {
    @Override
    public String getName() {
        return "listener of mail job";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println("取消执行：\t "+context.getJobDetail().getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println("准备执行：\t "+context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println("执行结束：\t "+context.getJobDetail().getKey());
    }
}
