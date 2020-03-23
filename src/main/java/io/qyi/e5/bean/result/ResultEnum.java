package io.qyi.e5.bean.result;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-06 00:24
 **/
public enum ResultEnum {
    SUCCESS(0, "ok"),
    UNKNOWN_ERROR(-1, "unknown error"),
    INVALID_TOKEN(-10000,"Invalid token"),
    TOKEN_IS_NOT_FOUND(-10001,"This token was not found"),
    INVALID_TYPE(-10002,"Invalid type"),
    INVALID_FROM(-10003,"Invalid from"),
    TITLE_OR_MSG_IS_NULL(-10004,"title or msg is null"),
    INVALID_TOKEN_(-10005,"Invalid token"),
    NO_ROBOT_FOUND(-10006,"No QQ robot corresponding to this token was found"),
    NO_ROBOT_FOUND_(-10007,"No QQ robot corresponding to this token was found"),
    STATE_HAS_EXPIRED(-10008,"state has expired, please re-authorize."),
    INVALID_EMAIL(-10009,"Invalid Email!"),
    INVALID_format(-10010, "Invalid format");
    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
