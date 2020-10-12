package io.qyi.e5.config.security.bean.dto;

import lombok.Data;

/**
 * @program: demo
 * @description:
 * @author: 落叶随风
 * @create: 2020-07-08 15:59
 **/
@Data
public class PermissionListDto {
    String roleName;
    String url;

    public PermissionListDto(String roleName, String url) {
        this.roleName = roleName;
        this.url = url;
    }

    public PermissionListDto() {
    }
}
