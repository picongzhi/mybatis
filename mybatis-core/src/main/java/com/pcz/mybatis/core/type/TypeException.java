package com.pcz.mybatis.core.type;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 类型异常
 *
 * @author picongzhi
 */
public class TypeException extends PersistenceException {
    public TypeException() {
        super();
    }

    public TypeException(String msg) {
        super(msg);
    }

    public TypeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TypeException(Throwable cause) {
        super(cause);
    }
}
