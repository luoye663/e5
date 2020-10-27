package io.qyi.e5.outlook.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.mapper.OutlookMapper;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.util.netRequest.OkHttpClientUtil;
import io.qyi.e5.util.netRequest.OkHttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            return false;
        } else {
            int expires_in = jsonObject.getIntValue("expires_in");
            String access_token = jsonObject.getString("access_token");
            String refresh_token = jsonObject.getString("refresh_token");
            String id_token = jsonObject.getString("id_token");

            Outlook outlook = new Outlook();
            outlook.setAccessToken(access_token)
                    .setRefreshToken(refresh_token)
                    .setIdToken(id_token);
            UpdateWrapper<Outlook> outlookUpdateWrapper = new UpdateWrapper<>();
            outlookUpdateWrapper.eq("client_id", client_id);
            baseMapper.update(outlook, outlookUpdateWrapper);
            return true;
        }
    }

    @Override
    public boolean save(String client_id, String client_secret, int github_id) {
        if (github_id == 0) {
            return false;
        }
        QueryWrapper<Outlook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("github_id", github_id)
                .or().eq("client_id", client_id);
        Outlook outlook1 = baseMapper.selectOne(queryWrapper);
//      有数据
        if (outlook1 != null) {
            outlook1.setClientId(client_id)
                    .setClientSecret(client_secret);

            int i = baseMapper.update(outlook1, queryWrapper);
            if (i == 1) {
                return true;
            }
        } else {
            Outlook outlook = new Outlook();
            outlook.setClientId(client_id)
                    .setClientSecret(client_secret)
                    .setGithubId(github_id);
            int i = baseMapper.insert(outlook);
            if (i == 1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean saveRandomTime(int github_id, int cron_time, int cron_time_random_start, int cron_time_random_end) {
        if (github_id == 0) {
            return false;
        }
        UpdateWrapper<Outlook> Wrapper = new UpdateWrapper<>();
        Wrapper.eq("github_id", github_id);
        Outlook outlook = new Outlook();
        outlook.setCronTime(cron_time).setCronTimeRandomStart(cron_time_random_start).setCronTimeRandomEnd(cron_time_random_end);
        int update = baseMapper.update(outlook, Wrapper);
//      有数据
        if (update > 0) {
            return true;
        }
        return false;
    }

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
                throw new Exception("无法刷新令牌!code:3"  + message);
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
                throw new Exception("无法刷新令牌!code:2,错误消息: "+ json.getJSONObject("error").getString("message"));
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
            OkHttpClientUtil.doGet("https://graph.microsoft.com/v1.0/me/messages/" + id, null,head, null);
        }
        return count;
    }

    public String MailList(String access_token) throws Exception {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("Authorization", access_token);
        String s = OkHttpClientUtil.doGet("https://graph.microsoft.com/v1.0/me/messages?$select=sender,subject",null, head, null);
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
            throw new Exception("返回的access_token字段不存在,无法刷新令牌! 需要重新授权!" );
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
}
