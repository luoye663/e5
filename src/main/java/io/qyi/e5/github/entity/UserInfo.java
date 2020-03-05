package io.qyi.e5.github.entity;

import lombok.Data;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-24 15:20
 **/
@Data
public class UserInfo {
    String login;
    String name;
    int github_id;
    String node_id;
    String avatar_url;

}
