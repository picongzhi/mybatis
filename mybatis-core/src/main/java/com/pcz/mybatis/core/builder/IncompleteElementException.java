package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 不完整元素异常
 *
 * @author picongzhi
 */
public class IncompleteElementException extends PersistenceException {
    public IncompleteElementException() {
        super();
    }

    public IncompleteElementException(String msg) {
        super(msg);
    }

    public IncompleteElementException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IncompleteElementException(Throwable cause) {
        super(cause);
    }
}
