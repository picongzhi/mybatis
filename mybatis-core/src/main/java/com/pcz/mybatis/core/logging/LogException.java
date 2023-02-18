package com.pcz.mybatis.core.logging;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 日志异常
 *
 * @author picongzhi
 */
public class LogException extends PersistenceException {
    public LogException() {
        super();
    }

    public LogException(String msg) {
        super(msg);
    }

    public LogException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}
