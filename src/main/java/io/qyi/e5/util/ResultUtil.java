package io.qyi.e5.util;

import io.qyi.e5.bean.result.Result;
import io.qyi.e5.bean.result.ResultEnum;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-05 21:05
 **/
public class ResultUtil extends Throwable {



    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("ok");
        result.setData(object);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public static Result error(Integer code, long time, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(time);
        return result;
    }
    public static Result success(ResultEnum resultEnum, Object object) {
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        result.setData(object);
        return result;
    }


    public static Result error(ResultEnum msg) {
        Result result = new Result();
        result.setCode(msg.getCode());
        result.setMsg(msg.getMsg());
        return result;
    }
}
