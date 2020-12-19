package io.qyi.e5.outlook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.qyi.e5.outlook.entity.Outlook;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
public interface IOutlookService extends IService<Outlook> {

    boolean getTokenAndSave(String code, String client_id, String client_secret, String redirect_uri, String grant_type) throws Exception;

    Outlook insertOne(String name, String describe, int github_id);

    boolean save(String client_id, String client_secret, int outlook_id, int github_id);

    boolean saveRandomTime(int github_id, int cron_time, int outlook_id, int cron_time_random_start, int cron_time_random_end);

    int getMailList(Outlook outlook) throws Exception;

    List<Outlook> findAll();

    int deleteInfo(int github_id);

    List<Outlook> getOutlooklist(int github_id);

    void setPause(int github_id, int outlookId);

    void setStart(int github_id, int outlookId);

    /**
     * 更新数据
     *
     * @param github_id: github_id
     * @param outlookId: outlookId
     * @param outlook:   更新的数据
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:29
     * @Return: * @return: void
     */
    void update(int github_id, int outlookId, Outlook outlook);

    void delete(int github_id, int outlookId);

    boolean isStatusRun(int github_id, int outlookId);
}
