package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.logging.Log;
import com.pcz.mybatis.core.logging.LogFactory;

/**
 * 日志缓存
 *
 * @author picongzhi
 */
public class LoggingCache implements Cache {
    /**
     * 日志
     */
    private final Log log;

    /**
     * 委托
     */
    private final Cache delegate;

    protected int requests = 0;

    protected int hits = 0;

    public LoggingCache(Cache delegate) {
        this.delegate = delegate;
        this.log = LogFactory.getLog(getId());
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        requests++;

        final Object value = delegate.getObject(key);
        if (value != null) {
            hits++;
        }

        if (log.isDebugEnabled()) {
            log.debug("Cache hit Ratio [" + getId() + "]: " + getHitRatio());
        }

        return value;
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * 获取命中率
     *
     * @return 命中率
     */
    private double getHitRatio() {
        return (double) hits / (double) requests;
    }
}
