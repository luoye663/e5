package io.qyi.e5.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class LogAop {
    private static String MDC_KEY_USER_NAME = "userName";
    private static String MDC_KEY_REQ_ID = "reqId";

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void logAdvicePointcut(){

    }

    @Before("logAdvicePointcut()")
    public void logAdvice(JoinPoint joinPoint){
        MDC.put(MDC_KEY_REQ_ID, UUID.randomUUID().toString());

    }

    @After("logAdvicePointcut()")
    public void afterLogAdvice(){
        MDC.remove(MDC_KEY_REQ_ID);
    }
}
