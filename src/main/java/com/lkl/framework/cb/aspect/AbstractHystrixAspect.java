package com.lkl.framework.cb.aspect;

import com.lkl.framework.cb.exceptions.CircuitBreakException;
import com.lkl.framework.cb.utils.AopUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaokailin on 16/10/20.
 */
public abstract class AbstractHystrixAspect {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * 该方法直接由aop环绕通知方法调用即可,无需额外处理
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    protected Object invokeAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        if (!needHystrixCommand(joinPoint)) {
            return AopUtils.directInvokeTarget(joinPoint);
        }

        HystrixCommand.Setter setter = createAndGetSetter(joinPoint);

        if (setter == null) {
            return AopUtils.directInvokeTarget(joinPoint);
        }

        return invokeCommand(setter, joinPoint);

    }

    /**
     * 获取HystrixCommand.Setter
     *
     * @param joinPoint
     * @return
     */
    protected abstract HystrixCommand.Setter createAndGetSetter(ProceedingJoinPoint joinPoint);

    /**
     * 判断是否需要使用Hystrix熔断
     *
     * @param joinPoint
     * @return
     */
    protected abstract boolean needHystrixCommand(ProceedingJoinPoint joinPoint);

    /**
     * 执行Hystrix Command
     *
     * @return
     */
    private Object invokeCommand(HystrixCommand.Setter setter, ProceedingJoinPoint joinPoint) {

        try {
            HystrixRequestContext.initializeContext();
            return new AbstractHystrixCommand(setter, joinPoint).execute();
        } catch (Exception e) {
            if (e instanceof HystrixRuntimeException) {
                HystrixRuntimeException hystrixRuntimeException = (HystrixRuntimeException) e;
                if (HystrixRuntimeException.FailureType.SHORTCIRCUIT == hystrixRuntimeException.getFailureType()) {
                    throw new CircuitBreakException(e);
                }
            }
            throw handleOpsException(e);
        } finally {
            HystrixRequestContext ctx = HystrixRequestContext.getContextForCurrentThread();
            if (ctx != null) {
                ctx.shutdown();
            }
        }
    }

    /**
     * 处理普通操作异常
     *
     * @param throwable
     * @return
     */
    public abstract RuntimeException handleOpsException(Throwable throwable);

    public class AbstractHystrixCommand extends HystrixCommand<Object> {

        private ProceedingJoinPoint joinPoint;
        private Throwable cause;

        public AbstractHystrixCommand(HystrixCommand.Setter setter, ProceedingJoinPoint joinPoint) {
            super(setter);
            this.joinPoint = joinPoint;
        }

        @Override
        protected Object run() throws Exception {

            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                this.cause = throwable;
                throw new RuntimeException(throwable);
            }
        }

        /**
         * 降级方法
         * 普通操作异常 {@see handleOpsException()}，熔断器开启抛出 {@link CircuitBreakException}
         *
         * @return
         */
        @Override
        protected Object getFallback() {
            RuntimeException exception = circuitBreaker.allowRequest() ? handleOpsException(cause) : new CircuitBreakException(cause);
            throw exception;
        }

    }
}
