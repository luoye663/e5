package io.qyi.e5.service.github;

import io.qyi.e5.github.entity.UserInfo;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-20 00:47
 **/
public interface GithubService {
    String getAccessToken(String code);

    String getUserEmail(String access_token) throws Exception;

    UserInfo getUserInfo(String access_token);

}
