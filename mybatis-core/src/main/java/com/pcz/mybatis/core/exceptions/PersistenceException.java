package com.pcz.mybatis.core.exceptions;

/**
 * 持久化异常
 *
 * @author picongzhi
 */
public class PersistenceException extends RuntimeException {
    public PersistenceException() {
        super();
    }

    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
