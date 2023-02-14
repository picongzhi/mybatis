package com.pcz.mybatis.core.session;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * SQL 会话异常
 *
 * @author picongzhi
 */
public class SqlSessionException extends PersistenceException {
    public SqlSessionException() {
        super();
    }

    public SqlSessionException(String msg) {
        super(msg);
    }

    public SqlSessionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SqlSessionException(Throwable cause) {
        super(cause);
    }
}
