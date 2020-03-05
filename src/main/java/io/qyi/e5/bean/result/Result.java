package io.qyi.e5.bean.result;

import lombok.Data;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2019-12-26 15:21
 **/
@Data
public class Result<T> {
    /** 错误码. */
    private Integer code;

    /** 提示信息. */
    private String msg;

    /** 具体的内容. */
    private T data;
}
