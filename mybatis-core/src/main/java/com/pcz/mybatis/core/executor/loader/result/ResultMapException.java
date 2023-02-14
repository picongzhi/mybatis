package com.pcz.mybatis.core.executor.loader.result;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 结果映射异常
 *
 * @author picongzhi
 */
public class ResultMapException extends PersistenceException {
    public ResultMapException() {
        super();
    }

    public ResultMapException(String msg) {
        super(msg);
    }

    public ResultMapException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ResultMapException(Throwable cause) {
        super(cause);
    }
}
