package io.qyi.e5.outlook_log.service.impl;

import io.qyi.e5.outlook_log.entity.OutlookLog;
import io.qyi.e5.outlook_log.mapper.OutlookLogMapper;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
@Service
public class OutlookLogServiceImpl extends ServiceImpl<OutlookLogMapper, OutlookLog> implements IOutlookLogService {
    @Override
    public void addLog(int githubId, String msg, String result,String original_msg) {
        OutlookLog outlookLog = new OutlookLog();
        outlookLog.setGithubId(githubId)
                .setResult(result.equals("1") ? "1" : "0")
                .setCallTime(String.valueOf(System.currentTimeMillis() / 1000))
                .setMsg(msg)
                .setOriginalMsg(original_msg);

        baseMapper.insert(outlookLog);
    }
}
