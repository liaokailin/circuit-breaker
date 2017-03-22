package com.lkl.framework.cb.redis;

import com.lkl.framework.cb.aspect.BeanHystrixAspect;
import com.lkl.framework.cb.exceptions.RedisOpsException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

/**
 * Created by liaokailin on 16/10/21.
 */
@Component
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnBean(JedisCluster.class)
@ConditionalOnProperty(prefix = "lkl.framework.cb.redis", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RedisHystrixAspect extends BeanHystrixAspect<JedisCluster> {

    /**
     * point cut
     */
    @Pointcut("execution(public * redis.clients.jedis.JedisCluster.*(..))")
    private void redisClusterPointCut() {
    }

    @Around("redisClusterPointCut()")
    public Object redisAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.invokeAspect(joinPoint);
    }


    @Override
    public RuntimeException handleOpsException(Throwable throwable) {
        return new RedisOpsException(throwable);
    }
}
