package io.qyi.e5.controller.quartz;

import io.qyi.e5.bean.AppQuartz;
import io.qyi.e5.service.quartz.JobUtil;
import io.qyi.e5.service.quartz.MyQuartzJobService;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-03 14:59
 **/
@Controller
@RequestMapping("/quartz")
public class QuartzController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${qz_cron}")
    private String qz_cron;

    @Autowired
    private JobUtil jobUtil;

    @GetMapping("/add")
    public void add() throws Exception {
        logger.info("添加定时任务");
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("aaa", "test");
        JobKey jobKey = new JobKey("crom任务", "我的cron任务组名1");
        TriggerKey triggerKey1 = new TriggerKey("我的cron触发器名1", "我的cron触发器组名1");

        AppQuartz appQuartz = new AppQuartz();
        appQuartz.setJobGroup("t1");
        appQuartz.setJobName("t1");
        appQuartz.setQuartzId(1);
        appQuartz.setCronExpression(qz_cron);
        appQuartz.setStartTime("2020-03-03 16:03:11");
        jobUtil.addJob(appQuartz,MyQuartzJobService.class);

//        quartzManager.addJob(jobKey,triggerKey1, MyQuartzJobService.class, "0/20 * * * * ?", jobDataMap);

    }
}
