package io.qyi.e5.config.exception;

import io.qyi.e5.config.APiCode;
import lombok.Getter;

/**
 * @program: push
 * @description:
 * @author: 落叶随风
 * @create: 2020-09-15 16:42
 **/
@Getter
public class APIException extends RuntimeException {
    private int code;
    private String msg;

    public APIException() {
        this(1001, "接口错误");
    }

    public APIException(String msg) {
        this(1002, msg);
    }

    public APIException(APiCode code) {
        this(code.getCode(), code.getMsg());
    }

    public APIException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
