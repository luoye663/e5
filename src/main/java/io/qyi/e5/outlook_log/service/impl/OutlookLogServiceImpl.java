package io.qyi.e5.outlook_log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import io.qyi.e5.outlook_log.entity.OutlookLog;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
@Service
public class OutlookLogServiceImpl implements IOutlookLogService {



    @Override
    public void addLog(int githubId, int outlookId, String msg, int result, String original_msg) {
        // OutlookLog outlookLog = new OutlookLog();
        // outlookLog.setGithubId(githubId)
        //         .setOutlookId(outlookId)
        //         .setResult(result)
        //         .setMsg(msg)
        //         .setOriginalMsg(original_msg);

        // baseMapper.insert(outlookLog);


    }

    @Override
    public int deleteInfo(int github_id) {
        QueryWrapper<OutlookLog> outlookLogQueryWrapper = new QueryWrapper<>();
        outlookLogQueryWrapper.eq("github_id", github_id);
        // return baseMapper.delete(outlookLogQueryWrapper);
        return 0;
    }

    @Override
    public List<OutlookLog> findAllList(int githubId, int outlookId) {
        // return baseMapper.findAllList(githubId, outlookId);
        return null;
    }
}
