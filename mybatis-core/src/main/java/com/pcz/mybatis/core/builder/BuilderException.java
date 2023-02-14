package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 构造器异常
 *
 * @author picongzhi
 */
public class BuilderException extends PersistenceException {
    public BuilderException() {
        super();
    }

    public BuilderException(String msg) {
        super(msg);
    }

    public BuilderException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BuilderException(Throwable cause) {
        super(cause);
    }
}
