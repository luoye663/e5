package io.qyi.e5.config;

import lombok.Getter;

/**
 * @program: push
 * @description:
 * @author: 落叶随风
 * @create: 2020-09-15 16:47
 **/
@Getter
public enum ResultCode {
    SUCCESS(0, "Success"),
    FAILED(1001, "响应失败"),
    API_FAILED(1001, "API 响应失败"),
    VALIDATE_FAILED(1002, "参数校验失败"),
    ERROR(5000, "未知错误"),
    NO_PARAMETERS(1003, "缺少参数");

    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
