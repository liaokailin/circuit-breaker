package com.lkl.framework.cb.aspect;

import com.lkl.framework.cb.annotations.CusHystrixCommand;
import com.lkl.framework.cb.exceptions.AnnotationHystrixException;
import com.lkl.framework.cb.utils.AopUtils;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by liaokailin on 16/10/20.
 */
@Component
@Aspect
@ConditionalOnProperty(prefix = "lkl.framework.cb.hystrix.command", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AnnotationHystrixAspect extends AbstractHystrixAspect {

    @Pointcut("@annotation(com.lkl.framework.cb.annotations.CusHystrixCommand)")
    public void hystrixCommandAnnotationPointcut() {
    }


    @Around("hystrixCommandAnnotationPointcut()")
    public Object annotationAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.invokeAspect(joinPoint);
    }


    @Override
    protected com.netflix.hystrix.HystrixCommand.Setter createAndGetSetter(ProceedingJoinPoint joinPoint) {
        String groupKey = AopUtils.getSimpleName(joinPoint);
        Method method = AopUtils.getMethod(joinPoint);
        if (groupKey == null || method == null) {
            throw new RuntimeException("retrieve joinPoint metadata fail.");
        }
        CusHystrixCommand enniuHystrixCommand = method.getAnnotation(CusHystrixCommand.class);
        String commandKey = StringUtils.isNotEmpty(enniuHystrixCommand.value()) ? enniuHystrixCommand.value() : groupKey + "." + method.getName();

        com.netflix.hystrix.HystrixCommand.Setter setter = com.netflix.hystrix.HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
        return setter;
    }

    @Override
    protected boolean needHystrixCommand(ProceedingJoinPoint joinPoint) {
        return true;
    }

    @Override
    public RuntimeException handleOpsException(Throwable throwable) {
        return new AnnotationHystrixException(throwable);
    }
}
