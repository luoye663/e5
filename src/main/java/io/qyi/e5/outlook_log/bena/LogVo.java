package io.qyi.e5.outlook_log.bena;

import lombok.Data;

/**
 * 返回给浏览器的字段
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-14 15:05
 **/
@Data
public class LogVo {
    private int callTime;
    private int result;
    private String msg;
    private String originalMsg;
}
