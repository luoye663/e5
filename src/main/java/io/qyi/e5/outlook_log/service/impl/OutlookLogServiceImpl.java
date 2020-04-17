package io.qyi.e5.outlook_log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public void addLog(int githubId, String msg, int result,String original_msg) {
        OutlookLog outlookLog = new OutlookLog();
        outlookLog.setGithubId(githubId)
                .setResult(result)
                .setCallTime((int) (System.currentTimeMillis() / 1000))
                .setMsg(msg)
                .setOriginalMsg(original_msg);

        baseMapper.insert(outlookLog);
    }

    @Override
    public int deleteInfo(int github_id) {
        QueryWrapper<OutlookLog> outlookLogQueryWrapper = new QueryWrapper<>();
        outlookLogQueryWrapper.eq("github_id", github_id);
        return baseMapper.delete(outlookLogQueryWrapper);
    }
}
