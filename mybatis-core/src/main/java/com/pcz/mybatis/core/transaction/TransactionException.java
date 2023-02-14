package com.pcz.mybatis.core.transaction;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 事务异常
 *
 * @author picongzhi
 */
public class TransactionException extends PersistenceException {
    public TransactionException() {
        super();
    }

    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
