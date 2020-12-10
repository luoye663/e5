package io.qyi.e5.config;

import lombok.Getter;

/**
 * @program: push
 * @description:
 * @author: 落叶随风
 * @create: 2020-09-30 10:48
 **/
@Getter
public enum APiCode {
    TOKEN_LENGTH_IS_INCORRECT(10000, "token长度不正确"),
    TOKEN_DOES_NOT_EXIST(10000, "token不存在"),
    OUTLOOK_QUANTITATIVE(10001, "此账号数量已超过5个"),
    GITHUBID_NOT_NULL(10002, "github id 不能为空"),
    OUTLOOK_NAME_NOT_NULL(10002, "outlook名称不能为空!"),
    OUTLOOK_INSERT_ERROR(10004, "新增失败!");


    private int code;
    private String msg;

    APiCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
