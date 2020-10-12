package io.qyi.e5.config.security.bean.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @program: wds
 * @description:
 * @author: 落叶随风
 * @create: 2020-07-09 17:09
 **/
@Data
public class RoleMenuDto {
    private int id;
    /*菜单名*/
    private String name;
    /*url*/
    private String url;
    /*父id*/
    private int pid;

    private List<Object> data = new LinkedList<>();
}
