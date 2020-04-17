package io.qyi.e5.outlook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.qyi.e5.outlook.entity.Outlook;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
public interface IOutlookService extends IService<Outlook> {

    boolean getTokenAndSave(String code,String client_id,String client_secret,String redirect_uri,String grant_type) throws Exception;

    boolean save(String client_id,String client_secret,int github_id);

    boolean saveRandomTime(int github_id,int cron_time,int cron_time_random_start,int cron_time_random_end);

    boolean getMailList(Outlook outlook);

    List<Outlook> findAll();

    int deleteInfo(int github_id);
}
