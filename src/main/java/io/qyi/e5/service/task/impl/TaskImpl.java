package io.qyi.e5.service.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.outlook.bean.OutlookMq;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-16 16:53
 **/
@Service
@Slf4j
public class TaskImpl implements ITask {

    @Autowired
    IOutlookService outlookService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IOutlookLogService outlookLogService;

    @Value("${outlook.error.countMax}")
    int errorCountMax;



    /**
     * 更新下次调用时间
     * TODO 这一步待删除
     * @param github_id:
     * @param outlookId:
     * @Author: 落叶随风
     * @Date: 2021/7/26  15:41
     * @Return: * @return: void
     */
    @Override
    public void updateOutlookExecDateTime(int github_id, int outlookId) {
        Outlook Outlook = outlookService.getOne(new QueryWrapper<Outlook>().eq("github_id", github_id).eq("id", outlookId));
        if (Outlook == null) {
            log.warn("未找到此用户,github_id: {}", github_id);
            return;
        }
        /*根据用户设置生成随机数*/
        int Expiration = getRandom(Outlook.getCronTimeRandomStart(), Outlook.getCronTimeRandomEnd());
        Outlook ol =  new Outlook();
        ol.setId(outlookId).setGithubId(github_id);
        ol.setNextTime((int) ((System.currentTimeMillis() / 1000) + Expiration));
        outlookService.update(ol);

        /*将此用户信息加入redis，如果存在则代表在队列中，同时提前10秒过期*/
       /* String rsKey = "user.mq:" + github_id + ".outlookId:" + outlookId;
        if (!redisUtil.hasKey(rsKey)) {
            redisUtil.set(rsKey, (System.currentTimeMillis() / 1000) + Expiration, Expiration - 10);

            Outlook ol =  new Outlook();
            ol.setId(outlookId).setGithubId(github_id);
            ol.setNextTime((int) ((System.currentTimeMillis() / 1000) + Expiration));
            outlookService.update(ol);
            // send(mq, Expiration * 1000);
        } else {
            log.info("Key 存在,不执行{}",rsKey);
        }*/
    }

    /**
     * 将所有outlook账户列表加入队列
     * @Author: 落叶随风
     * @Date: 2021/7/26  15:40
     * @Return: * @return: void
     */
    @Override
    public void sendTaskOutlookMQALL() {
        List<Outlook> all = outlookService.findAll();
        Iterator<Outlook> iterator = all.iterator();
        while (iterator.hasNext()) {
            Outlook next = iterator.next();
            /*根据用户设置生成随机数*/
            int Expiration = getRandom(next.getCronTimeRandomStart(), next.getCronTimeRandomEnd());
            /*将此用户信息加入redis，如果存在则代表在队列中，同时提前10秒过期*/
            if (!redisUtil.hasKey("user.mq:" + next.getGithubId())) {
                redisUtil.set("user.mq:" + next.getGithubId(), 0, Expiration - 10);
                // send(next.getGithubId(), Expiration * 1000);
            }
        }
    }

    /**
     * 调用一次邮件
     * @param github_id: github_id
     * @param outlookId: outlookId
     * @Author: 落叶随风
     * @Date: 2021/7/26  15:39
     * @Return: * @return: boolean
     */
    @Override
    public boolean executeE5(int github_id,int outlookId) {
        Outlook Outlook = outlookService.getOne(new QueryWrapper<Outlook>().eq("github_id", github_id).eq("id",outlookId));
        if (Outlook == null) {
            log.warn("未找到此用户,github_id: {}", github_id);
            return false;
        }
        boolean isExecuteE5;
        String errorKey = "user.mq:" + github_id + ":outlook.id:" + outlookId + ":error";
        try {
            int mail_count = outlookService.getMailList(Outlook);
            outlookLogService.addLog(github_id,outlookId, "ok", 1, "读取邮件数量:" + mail_count);
            if (redisUtil.hasKey(errorKey)) {
                redisUtil.del(errorKey);
            }
            isExecuteE5 = true;
        } catch (Exception e) {
            /*连续错误判断*/
            if (!redisUtil.hasKey(errorKey)) {
                redisUtil.set(errorKey, 1);
                isExecuteE5 = true;
            } else {
                int error_count = (int) redisUtil.get(errorKey);
                if (error_count >= errorCountMax) {
                    outlookLogService.addLog(github_id, outlookId,"error", 0, e.getMessage());
                    outlookLogService.addLog(github_id, outlookId,"error", 0, "检测到3次连续错误，下次将不再自动调用，请修正错误后再授权开启续订。");
                    /*设置状态为停止*/
                    Outlook outlook = new Outlook();
                    outlook.setStatus(5).setId(outlookId).setGithubId(github_id);
                    outlookService.update(outlook);
                    isExecuteE5 = false;
                } else {
                    redisUtil.incr(errorKey, 1);
                    outlookLogService.addLog(github_id, outlookId,"error", 0, e.getMessage());
                    isExecuteE5 = true;
                }
            }
        }
        return isExecuteE5;
    }


    @Override
    public void listen(OutlookMq mq) {
        boolean b = executeE5(mq.getGithubId(),mq.getOutlookId());
        /*再次进行添加任务*/
        if (b) {
            if (outlookService.isStatusRun(mq.getGithubId(), mq.getOutlookId())) {
                updateOutlookExecDateTime(mq.getGithubId(), mq.getOutlookId());
            } else {
                outlookLogService.addLog(mq.getGithubId(), mq.getOutlookId(), "error", 0, "检测到手动设置了运行状态，停止调用!");
            }
        } else {
            outlookLogService.addLog(mq.getGithubId(), mq.getOutlookId(), "error", 0, "执行失败,结束调用!");
        }
    }

    /**
     * 生成随机数
     *
     * @param end
     * @Description:
     * @param: * @param start
     * @return: java.lang.String
     * @Author: 落叶随风
     * @Date: 2020/4/16
     */
    public int getRandom(int start, int end) {
        Random r = new Random();
        int Expiration = (r.nextInt(end - start + 1) + start);
        return Expiration;
    }


}
