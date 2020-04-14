package io.qyi.e5.outlook_log.service;

import io.qyi.e5.outlook_log.entity.OutlookLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
public interface IOutlookLogService extends IService<OutlookLog> {
    void addLog(int githubId, String msg,int result,String original_msg);

}
