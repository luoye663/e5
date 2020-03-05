package io.qyi.e5.service.github.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.qyi.e5.github.entity.UserInfo;
import io.qyi.e5.service.github.GithubService;
import io.qyi.e5.util.StringUtil;
import io.qyi.e5.util.netRequest.OkHttpRequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-20 00:56
 **/
@Service("GithubService")
public class GithubServiceImpl implements GithubService {
    @Value("${github.client_id}")
    private String client_id;
    @Value("${github.client_secret}")
    private String client_secret;

    @Override
    public String getAccessToken(String code)  {
        Map<String, Object> par = new HashMap<>();
        par.put("client_id", client_id);
        par.put("client_secret", client_secret);
        par.put("code", code);
        Map<String, Object> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded");
        String s = null;
        try {
            s = OkHttpRequestUtils.doPost("https://github.com/login/oauth/access_token", head, par);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(s);
        Map<String, String> map = StringUtil.ParsingUrl(s);
        return map.get("access_token");
    }

    @Override
    public String getUserEmail(String access_token) throws Exception {
        Map<String, Object> head = new HashMap<>();
        head.put("Authorization", "token " + access_token);
        head.put("Content-Type", "application/vnd.github.machine-man-preview+json");
        String s = OkHttpRequestUtils.doGet("https://api.github.com/user/emails", head, null);
        System.out.println(s);
        JSONArray jsonArray = JSON.parseArray(s);
        if (!jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//              电子邮件是主要的并且已通过验证。
                if (jsonObject.getBoolean("primary") && jsonObject.getBoolean("verified")) {
                    return jsonObject.getString("email");
                }
            }
        }
        return null;
    }

    @Override
    public UserInfo getUserInfo(String access_token)  {
        Map<String, Object> head = new HashMap<>();
        head.put("Authorization", "token " + access_token);
        head.put("Content-Type", "application/vnd.github.machine-man-preview+json");
        try {
            String s = OkHttpRequestUtils.doGet("https://api.github.com/user", head, null);
            JSONObject jsonObject = JSON.parseObject(s);
            UserInfo userInfo = new UserInfo();
            if (!jsonObject.isEmpty()) {
                userInfo.setLogin(jsonObject.getString("login"));
                userInfo.setName(jsonObject.getString("name"));
                userInfo.setAvatar_url(jsonObject.getString("avatar_url"));
                userInfo.setGithub_id(jsonObject.getIntValue("id"));
                userInfo.setNode_id(jsonObject.getString("node_id"));
            }
            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
