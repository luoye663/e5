package io.qyi.e5.service.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-03 14:37
 **/
public class MyQuartzJobService implements Job {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IOutlookService outlookService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("定时任务被调用~");
//        QueryWrapper<Outlook> queryWrapper = new QueryWrapper<>();
        List<Outlook> list = outlookService.findAll();
        logger.info(String.valueOf(list.size()));
        for (Outlook outlook :list) {
            logger.info(outlook.toString());
            outlookService.getMailList(outlook);
        }
    }
}
