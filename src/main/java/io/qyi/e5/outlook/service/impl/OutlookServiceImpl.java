package io.qyi.e5.outlook.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.qyi.e5.config.APiCode;
import io.qyi.e5.config.exception.APIException;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.mapper.OutlookMapper;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.util.netRequest.*;
import io.qyi.e5.util.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
@Service
public class OutlookServiceImpl extends ServiceImpl<OutlookMapper, Outlook> implements IOutlookService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${outlook.errorMsg}")
    private String[] errorMsg;

    @Autowired
    RedisUtil redisUtil;

    // 2020-03-2 10:38 这里需要进行查询判断数据库是否有内容再进行插入。
    @Override
    public boolean getTokenAndSave(String code, String client_id, String client_secret, String redirect_uri, String grant_type) throws Exception {
        Map<String, Object> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> par = new HashMap<>();
        par.put("client_id", client_id);
        par.put("client_secret", client_secret);
        par.put("code", code);
        par.put("redirect_uri", redirect_uri);
        par.put("grant_type", grant_type);
        String s = OkHttpClientUtil.doPost("https://login.microsoftonline.com/common/oauth2/v2.0/token", head, par);
        JSONObject jsonObject = JSON.parseObject(s);
        logger.info("请求access_token返回数据：" + s);
        if (jsonObject.get("error") != null) {
            logger.error("错授权误!");
            throw new APIException(jsonObject.get("error_description").toString());
        } else {
            int expires_in = jsonObject.getIntValue("expires_in");
            String access_token = jsonObject.getString("access_token");
            String refresh_token = jsonObject.getString("refresh_token");
            String id_token = jsonObject.getString("id_token");

            Outlook outlook = new Outlook();
            outlook.setAccessToken(access_token)
                    .setRefreshToken(refresh_token)
                    .setStatus(3)

                    .setIdToken(id_token);
            UpdateWrapper<Outlook> outlookUpdateWrapper = new UpdateWrapper<>();
            outlookUpdateWrapper.eq("client_id", client_id);
            baseMapper.update(outlook, outlookUpdateWrapper);
            return true;
        }
    }

    /*
     *
     * @param name: 插入一条新列表
     * @param describe: 描述
     * @param github_id:  github_id
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:25
     * @Return: * @return: io.qyi.e5.outlook.entity.Outlook
    */
    @Override
    public Outlook insertOne(String name, String describe, int github_id) {
        if (StringUtils.isBlank(name)) {
            throw new APIException(APiCode.OUTLOOK_NAME_NOT_NULL);
        }
        Outlook outlook = new Outlook();
        outlook.setName(name);
        outlook.setDescribes(describe);
        outlook.setGithubId(github_id);
        logger.info(outlook.toString());
        if (baseMapper.insert(outlook) != 1) {
            throw new APIException(APiCode.OUTLOOK_INSERT_ERROR);
        }
        return outlook;
    }
    /*
     * 保存key
     * @param client_id:
     * @param client_secret:
     * @param outlook_id:
     * @param github_id:
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:24
     * @Return: * @return: boolean
    */
    @Override
    public boolean save(String client_id, String client_secret, int outlook_id, int github_id) {
        if (github_id == 0) {
            throw new APIException(APiCode.OUTLOOK_NAME_NOT_NULL);
        }
        if (outlook_id == 0 || StringUtils.isBlank(client_id) || StringUtils.isBlank(client_secret)) {
            throw new APIException("缺少参数!");
        }
        QueryWrapper<Outlook> queryWrapper = new QueryWrapper<>();
//        HashMap<String, Object> sc = new HashMap<>();
//        sc.put("github_id", github_id);
//        sc.put("id", outlook_id);
//        queryWrapper.allEq(sc);
        queryWrapper.eq("github_id", github_id).eq("id", outlook_id);
        /*2020-12-10 mybatis plus问题导致会被截断*/
//        Outlook outlook1 = baseMapper.selectOne(queryWrapper);

        Outlook outlook1 = baseMapper.selectOutlookOne(outlook_id, github_id);
        if (outlook1 == null) {
            throw new APIException("未查询到此条记录!");
        }
        outlook1.setClientId(client_id);
        outlook1.setClientSecret(client_secret);
        outlook1.setStep(1)
                .setStatus(8);
        ;
        if (baseMapper.update(outlook1, queryWrapper) != 1) {
            throw new APIException("更新记录失败!");
        }
        return true;
    }
    /*
     * 保存随机调用时间
     * @param github_id: github_id
     * @param cron_time: cron_time
     * @param outlook_id: outlook_id
     * @param cron_time_random_start: 开始时间
     * @param cron_time_random_end:  结束时间
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:24
     * @Return: * @return: boolean
    */
    @Override
    public boolean saveRandomTime(int github_id, int cron_time, int outlook_id, int cron_time_random_start, int cron_time_random_end) {
        if (github_id == 0 || outlook_id == 0) {
            throw new APIException("缺少参数!");
        }
        UpdateWrapper<Outlook> Wrapper = new UpdateWrapper<>();
        Wrapper.eq("github_id", github_id);
        Wrapper.eq("id", outlook_id);
        Outlook outlook = new Outlook();
        outlook.setCronTime(cron_time)
                .setCronTimeRandomStart(cron_time_random_start)
                .setCronTimeRandomEnd(cron_time_random_end)
                .setStep(2)
                .setStatus(6);

        int update = baseMapper.update(outlook, Wrapper);
//      有数据
        if (update > 0) {
            return true;
        }
        return false;
    }
    /*
     * 查询所有列表
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:23
     * @Return: * @return: java.util.List<io.qyi.e5.outlook.entity.Outlook>
    */
    @Override
    public List<Outlook> findAll() {
        return baseMapper.selectList(null);
    }

    /**
     * 删除用户outlook
     *
     * @Description:
     * @param: * @param github_id
     * @return: int
     * @Author: 落叶随风
     * @Date: 2020/4/17
     */
    @Override
    public int deleteInfo(int github_id) {
        QueryWrapper<Outlook> outlookQueryWrapper = new QueryWrapper<>();
        outlookQueryWrapper.eq("github_id", github_id);
        return baseMapper.delete(outlookQueryWrapper);
    }

    /*
     * 调用邮件列表
     * @param outlook:
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:22
     * @Return: * @return: int
    */
    @Override
    public int getMailList(Outlook outlook) throws Exception {
        String s = MailList(outlook.getAccessToken());
        JSONObject json = JSON.parseObject(s);
        /*错误情况，一般是令牌过期*/
        if (json.containsKey("error")) {
            String code = json.getJSONObject("error").getString("code");
            String message = json.getJSONObject("error").getString("message");
            /*如果出现的错误是没有message中收集的，那么就认为是无法刷新的情况。比如 用户取消了授权、删除了key*/
            if (!errorCheck(message)) {
                throw new Exception("无法刷新令牌!code:3" + message);
            }
            logger.info("令牌过期!");
            /*刷新令牌*/
            String token = refresh_token(outlook);
            if (token == null) {
                throw new Exception("刷新令牌失败! refresh_token 为空!");
            }
            /*再次获取邮件列表*/
            s = MailList(token);
            json = JSON.parseObject(s);
            if (json.containsKey("error")) {
                throw new Exception("无法刷新令牌!code:2,错误消息: " + json.getJSONObject("error").getString("message"));
            }
        }
        logger.info("邮件列表请求成功!" + s);
        int mail_count = getMailBody(5, s, outlook.getAccessToken());
        logger.info("读取邮件数量: {}", mail_count);

        return mail_count;
    }

    /**
     * 读取邮件内容
     *
     * @Description:
     * @param: count 读取数量，0 则读取当前页所有
     * @return: void
     * @Author: 落叶随风
     * @Date: 2020/4/15
     */
    public int getMailBody(int count, String MailBody, String access_token) throws Exception {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(MailBody, JsonObject.class);
        int mial_list_count = jsonObject.get("value").getAsJsonArray().size();
        if (mial_list_count < 1) {
            return 0;
        }
        if (mial_list_count < count) {
            count = jsonObject.get("value").getAsJsonArray().size();
        }
        JsonArray value = jsonObject.get("value").getAsJsonArray();
        if (count == 0) {
            count = value.size();
        }
        for (int i = 0; i < count - 1; i++) {
            JsonObject mail = value.get(i).getAsJsonObject();
            String id = mail.get("id").getAsString();

            Map<String, String> head = new HashMap<>();
            head.put("Content-Type", "application/json");
            head.put("Authorization", access_token);
            /*不用管邮件内容*/
            OkHttpClientUtil.doGet("https://graph.microsoft.com/v1.0/me/messages/" + id, null, head, null);
        }
        return count;
    }

    /**
     * @title 获取邮件列表，默认5封
     * @description
     * @author 落叶随风
     * @param: access_token
     * @updateTime 2020/12/19 21:17
     * @return: java.lang.String
     * @throws
     */
    public String MailList(String access_token) throws Exception {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("Authorization", access_token);
        String s = OkHttpClientUtil.doGet("https://graph.microsoft.com/v1.0/me/messages?$select=sender,subject", null, head, null);
        logger.debug("请求邮件列表返回数据：" + s);
        return s;
    }


    // 刷新令牌，同时更新数据库中的令牌
    public String refresh_token(Outlook outlook) throws Exception {
        Map<String, Object> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> par = new HashMap<>();
        par.put("client_id", outlook.getClientId());
        par.put("scope", "openid offline_access Mail.Read");
        par.put("client_secret", outlook.getClientSecret());
        par.put("grant_type", "refresh_token");
        par.put("refresh_token", outlook.getRefreshToken());
        String s = null;
        s = OkHttpClientUtil.doPost("https://login.microsoftonline.com/common/oauth2/v2.0/token", head, par);
        logger.info("请求刷新列表返回数据：" + s);
        JSONObject jsonObject = JSON.parseObject(s);
        if (!jsonObject.containsKey("access_token")) {
            logger.info("返回的access_token字段不存在");
            throw new Exception("返回的access_token字段不存在,无法刷新令牌! 需要重新授权!");
        }
        outlook.setRefreshToken(jsonObject.getString("refresh_token"));
        outlook.setAccessToken(jsonObject.getString("access_token"));
        outlook.setIdToken(jsonObject.getString("id_token"));
        QueryWrapper<Outlook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("client_id", outlook.getClientId());
        if (baseMapper.update(outlook, queryWrapper) != 1) {
            logger.info("返更新行数不为1");
            throw new Exception("更新数据库时发现有重复的key");
        }
        return outlook.getAccessToken();
//            更新数据库
    }

    /**
     * 检查出现的错误是否能够刷新令牌
     *
     * @throws
     * @title errorCheck
     * @description
     * @author 落叶随风
     * @updateTime 2020/3/5 14:47
     */
    public boolean errorCheck(String msg) {
        System.out.println(Arrays.toString(errorMsg));
        for (String s : errorMsg) {
            if (msg.indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @title 获取本账号下的outlook 应用列表
     * @description
     * @author 落叶随风
     * @param: github_id
     * @updateTime 2020/12/19 21:16
     * @return: java.util.List<io.qyi.e5.outlook.entity.Outlook>
     * @throws
     */
    @Override
    public List<Outlook> getOutlooklist(int github_id) {
        QueryWrapper<Outlook> qw = new QueryWrapper<Outlook>().eq("github_id", github_id);
        List<Outlook> outlooks = baseMapper.selectList(qw);
        return outlooks;
    }

    /**
     * 设置暂停状态
     * @title setPause
     * @description
     * @author 落叶随风
     * @param: github_id
     * @param: outlookId
     * @updateTime 2020/12/19 21:16
     * @throws
     */
    @Override
    public void setPause(int github_id, int outlookId) {
        UpdateWrapper<Outlook> up = new UpdateWrapper<>();
        up.eq("github_id", github_id).eq("id", outlookId);
        Outlook outlook = baseMapper.selectOne(up);
        if (outlook == null) {
            throw new APIException("查无此记录!");
        }
        /*只允许运行状态的应用设置暂停*/
        if (outlook.getStatus() != 3) {
            throw new APIException("只允许 运行状态 的应用设置暂停!");
        }
        if (baseMapper.update(new Outlook().setStatus(2), up) != 1) {
            throw new APIException("更新失败!");
        }
    }

    /**
     * 设置开始状态
     * @title setStart
     * @description
     * @author 落叶随风
     * @param: github_id
     * @param: outlookId
     * @updateTime 2020/12/19 21:16
     * @throws
     */
    @Override
    public void setStart(int github_id, int outlookId) {
        UpdateWrapper<Outlook> up = new UpdateWrapper<>();
        up.eq("github_id", github_id).eq("id", outlookId);
        if (baseMapper.update(new Outlook().setStatus(6), up) != 1) {
            throw new APIException("更新失败!");
        }
    }
    /**
     *  更新数据
     * @param github_id:  github_id
     * @param outlookId: outlookId
     * @param outlook:  更新的数据
     * @Author: 落叶随风
     * @Date: 2020/12/19  21:29
     * @Return: * @return: void
    */
    @Override
    public void update(int github_id, int outlookId, Outlook outlook) {
        UpdateWrapper<Outlook> uw = new UpdateWrapper<>();
        uw.eq("id", outlookId);
        uw.eq("github_id", github_id);
        baseMapper.update(outlook, uw);
    }

    @Override
    public void delete(int github_id, int outlookId) {
        QueryWrapper<Outlook> wp = new QueryWrapper<>();
        wp.eq("github_id", github_id);
        wp.eq("id", outlookId);
        if (baseMapper.delete(wp) != 1) {
            log.error("删除数据失败! github_id:{github_id} - outlookId:{outlookId}");
            throw new APIException("删除失败!");
        }
    }
    @Override
    public boolean isStatusRun(int github_id, int outlookId){
        QueryWrapper<Outlook> wp = new QueryWrapper<>();
        wp.eq("github_id", github_id);
        wp.eq("id", outlookId);
        Outlook outlook = baseMapper.selectOne(wp);
        if (outlook != null) {
            if (outlook.getStatus() == 3) {
                return true;
            }
        }

        return false;
    }
}
