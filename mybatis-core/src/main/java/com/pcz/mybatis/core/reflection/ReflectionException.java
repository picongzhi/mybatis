package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 反射异常
 *
 * @author picongzhi
 */
public class ReflectionException extends PersistenceException {
    public ReflectionException() {
        super();
    }

    public ReflectionException(String msg) {
        super(msg);
    }

    public ReflectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
