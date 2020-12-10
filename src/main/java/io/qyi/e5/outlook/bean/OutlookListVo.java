package io.qyi.e5.outlook.bean;

import lombok.Data;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-12-10 16:54
 **/
@Data
public class OutlookListVo {
    private int id;
    private String clientId;
    private String clientSecret;
    private Integer cronTimeRandomStart;
    private Integer cronTimeRandomEnd;
    /*名称*/
    private String name;
    /*描述*/
    private String describes;
    /*下次调用时间*/
    private long nextTime;
    /*运行状态*/
    private int status;
}
