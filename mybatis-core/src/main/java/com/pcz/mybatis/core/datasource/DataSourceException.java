package com.pcz.mybatis.core.datasource;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 数据源异常
 *
 * @author picongzhi
 */
public class DataSourceException extends PersistenceException {
    public DataSourceException() {
        super();
    }

    public DataSourceException(String msg) {
        super(msg);
    }

    public DataSourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DataSourceException(Throwable cause) {
        super(cause);
    }
}
