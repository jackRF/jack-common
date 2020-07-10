package org.jack.common.config;

import com.alibaba.fastjson.JSON;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiMonitorAspect{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@within(org.springframework.stereotype.Controller)"
    +" && execution(public * com.sitco.point.controller.*.*(..))"
    +" && @annotation(org.springframework.web.bind.annotation.ResponseBody)")
    public void ajaxPointcut(){
    }
    @Before("ajaxPointcut()")
    public void before(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String methodName=methodSignature.getName();
        String[] parameterNames=methodSignature.getParameterNames();
        Object[] args=joinPoint.getArgs();
        StringBuilder format=new StringBuilder();
        format.append("request ").append(methodName).append(":");
        int i=-1;
        for(String parameterName:parameterNames){
            if(++i!=0){
                format.append(", ");
            }
            format.append(parameterName).append("={}");
        }
        logger.info(format.toString(),args);
    }
    // @AfterReturning(pointcut ="ajaxPointcut()",returning ="result")
    public void afterReturning(JoinPoint joinPoint,Object result){
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        logger.info("response {}:{}",methodSignature.getName(),JSON.toJSONString(result));
    }
    @AfterThrowing(pointcut ="ajaxPointcut()",throwing="ex")
    public void afterThrowing(JoinPoint point, Exception ex){
        logger.error(ex.getMessage(), ex);
    }
}