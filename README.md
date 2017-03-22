
# 熔断降级使用指南


## 简介

`circuit-breaker`在`hystrix`的基础上封装了redis的熔断、对bean的自定义熔断、使用注解对方法的熔断,该jar已在生产环境使用,没有明显的bug

## 快速开始


1.引入jar包

```
<dependency>
  <groupId>com.lkl.framework.cb</groupId>
  <artifactId>circuit-breaker</artifactId>
  <version>0.0.4-SNAPSHOT</version>
</dependency>
```

2.配置`lkl.framework.cb.redis.enabled = true`启用`redis`熔断降级功能



## 配置


### 开关

`lkl.framework.cb.hystrix.command.enabled`取值为`true`和`false`;`true`表示开启熔断降级功能,`false`反之.


### 其他配置

该功能底层依赖`hystrix`实现，因此配置可以参考`hystrix`的配置
```
# 线程池大小，修改需重启。
hystrix.threadpool.default.coreSize=10

# 排队线程数量阈值，达到时拒绝。(可动态修改)，该项配置仅在maxQueueSize!=-1的情况下有用
hystrix.threadpool.default.queueSizeRejectionThreshold=5

# 最大排队长度。默认-1，使用SynchronousQueue。其他值则使用 LinkedBlockingQueue。如果要从-1换成其他值则需重启
hystrix.threadpool.default.maxQueueSize=-1

# command线程执行超时时间，默认1s。(可动态修改)
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=1000

# 当在配置时间窗口内请求数量至少达到此数量并且错误率达到errorThresholdPercentage时，熔断。默认20个。(可动态修改)
hystrix.command.default.circuitBreaker.requestVolumeThreshold=20

# 短路多久以后开始尝试是否恢复，默认5s。(可动态修改)
hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds=5000

# 出错百分比阈值，当达到此阈值后，开始短路。默认50%。(可动态修改)
hystrix.command.default.circuitBreaker.errorThresholdPercentage=50

# 是否开启HystrixRequestLog。默认开启。(可动态修改)
hystrix.command.default.requestLog.enabled=true

########## Fallback 配置
#调用线程允许请求HystrixCommand.GetFallback()的最大数量，默认10。超出时将会有异常抛出。(可动态修改)
hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests=10

```

如果需要覆盖默认的配置，可以将配置中的**`default`**替换为`JedisCluter`对应的**`beanName`**.

例如：
　　默认线程池大小为`hystrix.threadpool.default.coreSize=10`,如果需要调整其大小，假设`JedisCluster`对应的`beanName`为`MyJedisCluster`
则覆盖线程池大小配置为`hystrix.threadpool.MyJedisCluster.coreSize=20`即可.


## 异常

异常名                   | 发生时期                  |
-------------------------|---------------------------|
RedisOpsException        |`redis`操作发生异常时抛出  |
CircuitBreakException    | 熔断器开启                |
AnnotationHystrixException | 注解方法操作发送异常时抛出 |

# 注解

CusHystrixCommand 可以自定义方法熔断,在方法上加上该注解

# 拓展

继承BeanHystrixAspect可快速为第三方包增加熔断降级功能

