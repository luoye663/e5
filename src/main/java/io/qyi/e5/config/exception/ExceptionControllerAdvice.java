package io.qyi.e5.config.exception;

import io.qyi.e5.config.ResultCode;
import io.qyi.e5.config.ResultVO;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @program: push
 * @description:
 * @author: 落叶随风
 * @create: 2020-09-15 16:41
 **/
@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     *
     * @param e: 用来捕获404，400这种无法到达controller的错误
     * @Author: 落叶随风
     * @Date: 2020/9/17  15:17
     * @Return: * @return: io.qyi.push.config.bean.ResultVO<java.lang.String>
     */
    @ExceptionHandler(APIException.class)
    public ResultVO<String> APIExceptionHandler(APIException e) {
        return new ResultVO<>(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResultVO<>(ResultCode.VALIDATE_FAILED, objectError.getDefaultMessage());
    }

    /**
     *  参数校验
     * @param e:
     * @Author: 落叶随风
     * @Date: 2020/9/21  11:28
     * @Return: * @return: io.qyi.push.config.bean.ResultVO<java.lang.String>
     */
    @ExceptionHandler(BindException.class)
    public ResultVO<String> BindException(BindException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);

        return new ResultVO<>(ResultCode.VALIDATE_FAILED, objectError.getDefaultMessage());
    }
    /**
     *  无参数
     * @param e:
     * @Author: 落叶随风
     * @Date: 2020/9/17  17:00
     * @Return: * @return: io.qyi.push.config.bean.ResultVO<java.lang.String>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVO<String> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResultVO<>(ResultCode.NO_PARAMETERS);
    }

    /*@RestControllerAdvice(basePackages = {"io.qyi.push.api.v1"}) // 注意哦，这里要加上需要扫描的包
    public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {
        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
            // 如果接口返回的类型本身就是ResultVO那就没有必要进行额外的操作，返回false
            return !returnType.getGenericParameterType().equals(ResultVO.class);
        }

        @Override
        public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
            // String类型不能直接包装，所以要进行些特别的处理
            if (returnType.getGenericParameterType().equals(String.class)) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // 将数据包装在ResultVO里后，再转换为json字符串响应给前端
                    return objectMapper.writeValueAsString(new ResultVO<>(data));
                } catch (JsonProcessingException e) {
                    throw new APIException("返回String类型错误");
                }
            }
            // 将原本的数据包装在ResultVO里
            return new ResultVO<>(data);
        }
    }*/
}
