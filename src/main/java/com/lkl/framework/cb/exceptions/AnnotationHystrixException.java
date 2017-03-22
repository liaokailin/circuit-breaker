package com.lkl.framework.cb.exceptions;

import com.lkl.framework.cb.annotations.CusHystrixCommand;

/**
 * 标注 {@link CusHystrixCommand} 注解后执行失败后抛出该异常
 * Created by liaokailin on 16/8/23.
 */
public class AnnotationHystrixException extends RuntimeException {
    public AnnotationHystrixException() {
    }

    public AnnotationHystrixException(String message) {
        super(message);
    }

    public AnnotationHystrixException(Throwable cause) {
        super(cause);
    }

    public AnnotationHystrixException(String message, Throwable cause) {
        super(message, cause);
    }
}
