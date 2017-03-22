package com.lkl.framework.cb.aspect;

import com.lkl.framework.cb.utils.AopUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * bean类型可以通过aop一刀切,一般用于处理第三方包和通用操作
 * 后期可考虑通过字节码生成对应子类,从而只需配置切入点即可,而不是继承BeanHystrixAspect
 * Created by liaokailin on 16/10/21.
 */
public abstract class BeanHystrixAspect<T> extends AbstractHystrixAspect implements ApplicationContextAware, InitializingBean {

    private Class<T> annotationClass;

    private ApplicationContext applicationContext;

    protected Map<String, EnniuSetter> setterMap = Maps.newHashMap();

    protected BeanHystrixAspect(){
        this.annotationClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(
                this.getClass(), BeanHystrixAspect.class);
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 处理配置信息
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {

            Map<String, T> sourceBeanMap = applicationContext.getBeansOfType(annotationClass);
            for (Iterator<String> iter = sourceBeanMap.keySet().iterator(); iter.hasNext(); ) { //防止出现空的Bean信息
                String beanName = iter.next();
                if (StringUtils.isNotEmpty(beanName)) {
                    T bean = sourceBeanMap.get(beanName);
                    if (bean != null) {
                        setterMap.put(bean.toString(), new EnniuSetter(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(beanName)), beanName));
                    }
                }
            }
            Preconditions.checkArgument(setterMap != null && setterMap.size() > 0, "after filter ,no available bean in spring context.");
        } catch (Exception e) {
            LOGGER.error("handle bean for hystrix setter occur exception");
        }

    }

    @Override
    protected HystrixCommand.Setter createAndGetSetter(ProceedingJoinPoint joinPoint) {
        EnniuSetter enniuSetter = setterMap.get(joinPoint.getTarget().toString());
        if(enniuSetter == null || !enniuSetter.check()){
            return null;
        }

        HystrixCommand.Setter setter = enniuSetter.setter;
        Method method = AopUtils.getMethod(joinPoint);
        if (method != null) {
            setter.andCommandKey(HystrixCommandKey.Factory.asKey(enniuSetter.beanName + "-" + method.getName()));
        } else {
            LOGGER.warn("obtain method name occur exception,will use command class name as key name.");
        }

        return setter;
    }

    @Override
    protected boolean needHystrixCommand(ProceedingJoinPoint joinPoint) {
       return true;
    }


    protected class EnniuSetter {
        HystrixCommand.Setter setter;
        String beanName;

        public EnniuSetter(HystrixCommand.Setter setter, String beanName) {
            this.setter = setter;
            this.beanName = beanName;
        }

        public boolean check() {
            return this.beanName != null && this.setter != null;
        }

    }

}