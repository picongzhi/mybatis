package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.Cache;

import java.util.concurrent.TimeUnit;

/**
 * 调度缓存
 *
 * @author picongzhi
 */
public class ScheduledCache implements Cache {
    /**
     * 委托
     */
    private final Cache delegate;

    /**
     * 清理时间间隔
     */
    protected long clearInterval;

    /**
     * 最近的清理时间
     */
    protected long lastClear;

    public ScheduledCache(Cache delegate) {
        this.delegate = delegate;
        this.clearInterval = TimeUnit.HOURS.toMillis(1);
        this.lastClear = System.currentTimeMillis();
    }

    public void setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        clearWhenStale();
        delegate.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return clearWhenStale()
                ? null
                : delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        clearWhenStale();
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        lastClear = System.currentTimeMillis();
        delegate.clear();
    }

    @Override
    public int getSize() {
        clearWhenStale();
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
     * 过期时清理
     *
     * @return 是否清理
     */
    private boolean clearWhenStale() {
        if (System.currentTimeMillis() - lastClear > clearInterval) {
            clear();
            return true;
        }
        return false;
    }
}
