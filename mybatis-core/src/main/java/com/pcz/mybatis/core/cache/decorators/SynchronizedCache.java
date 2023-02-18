package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.Cache;

/**
 * 同步的缓存
 *
 * @author picongzhi
 */
public class SynchronizedCache implements Cache {
    /**
     * 委托
     */
    private final Cache delegate;

    public SynchronizedCache(Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public synchronized void putObject(Object key, Object value) {
        delegate.putObject(key, value);
    }

    @Override
    public synchronized Object getObject(Object key) {
        return delegate.getObject(key);
    }

    @Override
    public synchronized Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public synchronized void clear() {
        delegate.clear();
    }

    @Override
    public synchronized int getSize() {
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
}
