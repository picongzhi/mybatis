package com.pcz.mybatis.core.cache;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 缓存异常
 *
 * @author picongzhi
 */
public class CacheException extends PersistenceException {
    public CacheException() {
        super();
    }

    public CacheException(String msg) {
        super(msg);
    }

    public CacheException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}
