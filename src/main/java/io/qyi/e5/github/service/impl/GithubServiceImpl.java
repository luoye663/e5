package io.qyi.e5.github.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.qyi.e5.github.entity.Github;
import io.qyi.e5.github.entity.UserInfo;
import io.qyi.e5.github.mapper.GithubMapper;
import io.qyi.e5.github.service.IGithubService;
import io.qyi.e5.util.StringUtil;
import io.qyi.e5.util.netRequest.OkHttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
@Service
public class GithubServiceImpl extends ServiceImpl<GithubMapper, Github> implements IGithubService {
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
            s = OkHttpClientUtil.doPost("https://github.com/login/oauth/access_token", head, par);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(s);
        Map<String, String> map = StringUtil.ParsingUrl(s);
        return map.get("access_token");
    }

    @Override
    public String getUserEmail(String access_token) throws Exception {
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "token " + access_token);
        head.put("Content-Type", "application/vnd.github.machine-man-preview+json");
        String s = OkHttpClientUtil.doGet("https://api.github.com/user/emails", null,head, null);
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
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "token " + access_token);
        head.put("Content-Type", "application/vnd.github.machine-man-preview+json");
        try {
            String s = OkHttpClientUtil.doGet("https://api.github.com/user",null, head, null);
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

    @Override
    public Github selectOne(QueryWrapper<Github> queryWrapper) {
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void insert(Github github) {
        baseMapper.insert(github);
    }

    /**
     * 删除此用户
    * @Description:
    * @param: * @param
    * @return: void
    * @Author: 落叶随风
    * @Date: 2020/4/17
    */
    @Override
    public int deleteInfo(int github_id) {
        QueryWrapper<Github> githubQueryWrapper = new QueryWrapper<>();
        githubQueryWrapper.eq("github_id", github_id);
        return baseMapper.delete(githubQueryWrapper);
    }
}
