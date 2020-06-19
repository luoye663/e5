package io.qyi.e5.bean.core;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qyi.e5.util.ResultUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-16 01:11
 **/
@Aspect
@Component
public class WebExceptionAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* io.qyi.e5..*.*(..))")
    private void bountyHunterPointcut() {
    }

    /**
     * 拦截web层异常，记录异常日志，并返回友好信息到前端
     *
     * @param e 异常对象
     */
    @AfterThrowing(pointcut = "bountyHunterPointcut()", throwing = "e")
    public void handleThrowing(JoinPoint joinPoint, Exception e) {
        long time = System.currentTimeMillis();
        logger.error("发现异常！方法：{} --->异常 {}, 异常ID: {}", joinPoint.getSignature().getName(), e, time);
        //这里输入友好性信息
//        writeContent(500, "十分抱歉，出现异常！程序猿小哥正在紧急抢修...", time);
        if (!StringUtils.isEmpty(e.getMessage())) {
            logger.error("异常", e.getMessage());
            writeContent(500, e.getMessage(),time);
        } else {
            writeContent(500, "十分抱歉，出现异常！程序猿小哥正在紧急抢修...", time);
        }
    }

    /**
     * 将内容输出到浏览器
     *
     * @param content 输出内容
     */
    public static void writeContent(Integer code, String content, long time) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/json;charset=UTF-8");
        response.setHeader("icop-content-type", "exception");
        PrintWriter writer = null;
        JsonGenerator jsonGenerator = null;
        try {
            writer = response.getWriter();
            jsonGenerator = (new ObjectMapper()).getFactory().createGenerator(writer);
            jsonGenerator.writeObject(ResultUtil.error(code, time, content));
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }

}
