package com.lkl.framework.cb.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by liaokailin on 16/10/20.
 */
public class AopUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(AopUtils.class);

    /**
     * 获取JoinPoint目标方法
     *
     * @param joinPoint
     * @return
     */
    public static Method getMethod(JoinPoint joinPoint) {
        try {
            return ((MethodSignature) joinPoint.getSignature()).getMethod();
        } catch (Exception e) {
            LOGGER.error("retrieve method of joinPoint target occur exception .", e);
        }
        return null;
    }

    /**
     * 获取目标类名
     * @param joinPoint
     * @return
     */
    public static String getSimpleName(JoinPoint joinPoint) {
        try{
        return joinPoint.getTarget().getClass().getSimpleName();
        }catch (Exception e){
            LOGGER.error("retrieve simple name of joinPoint target occur exception .", e);
        }
        return null;
    }

    /**
     * 直接执行目标方法
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    public static Object directInvokeTarget(ProceedingJoinPoint joinPoint)throws Throwable{
        LOGGER.info("aop direct invoke target method.");
        return joinPoint.proceed();
    }


}
