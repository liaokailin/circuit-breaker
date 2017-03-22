package com.lkl.framework.cb.exceptions;

/**
 * 熔断器开启异常
 * Created by liaokailin on 16/8/23.
 */
public class CircuitBreakException extends RuntimeException {
    public CircuitBreakException() {
    }

    public CircuitBreakException(String message) {
        super(message);
    }

    public CircuitBreakException(Throwable cause) {
        super(cause);
    }

    public CircuitBreakException(String message, Throwable cause) {
        super(message, cause);
    }
}
