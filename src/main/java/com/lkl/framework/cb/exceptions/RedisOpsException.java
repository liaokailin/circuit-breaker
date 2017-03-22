package com.lkl.framework.cb.exceptions;

/**
 * Redis操作异常
 * Created by liaokailin on 16/8/23.
 */
public class RedisOpsException extends RuntimeException {


    public RedisOpsException() {
    }

    public RedisOpsException(String message) {
        super(message);
    }

    public RedisOpsException(Throwable cause) {
        super(cause);
    }

    public RedisOpsException(String message, Throwable cause) {
        super(message, cause);
    }
}

