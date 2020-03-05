package io.qyi.e5.outlook.service;

import io.qyi.e5.outlook.entity.Outlook;
import com.baomidou.mybatisplus.extension.service.IService;

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

    boolean getMailList(Outlook outlook);

    List<Outlook> findAll();

}
