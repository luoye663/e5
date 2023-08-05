package io.qyi.e5.outlook.bean;

import lombok.Data;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-04 22:34
 **/
@Data
public class OutlookVo {
    private int id;
    private String clientId;
    private String clientSecret;
    private Integer cronTimeRandomStart;
    private Integer cronTimeRandomEnd;
    private Integer step;
    private Integer status;
    private Integer outlookId;
    private String tenantId;
}
