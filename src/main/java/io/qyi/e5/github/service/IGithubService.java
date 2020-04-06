package io.qyi.e5.github.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.github.entity.Github;
import com.baomidou.mybatisplus.extension.service.IService;
import io.qyi.e5.github.entity.UserInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
public interface IGithubService extends IService<Github> {
    String getAccessToken(String code);

    String getUserEmail(String access_token) throws Exception;

    UserInfo getUserInfo(String access_token);

    Github selectOne(QueryWrapper<Github> queryWrapper);

    void insert(Github github);
}
