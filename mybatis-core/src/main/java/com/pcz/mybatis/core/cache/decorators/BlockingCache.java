package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.cache.CacheException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞的缓存
 *
 * @author picongzhi
 */
public class BlockingCache implements Cache {
    /**
     * 阻塞超时时间
     */
    private long timeout;

    /**
     * 委托
     */
    private final Cache delegate;

    /**
     * 锁
     */
    private final ConcurrentHashMap<Object, CountDownLatch> locks;

    public BlockingCache(Cache delegate) {
        this.delegate = delegate;
        this.locks = new ConcurrentHashMap<>();
    }


    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        try {
            delegate.putObject(key, value);
        } finally {
            // 写入之后释放锁
            releaseLock(key);
        }
    }

    @Override
    public Object getObject(Object key) {
        // 阻塞获取锁
        acquireLock(key);

        Object value = delegate.getObject(key);
        if (value != null) {
            // 释放锁
            releaseLock(key);
        }

        return value;
    }

    @Override
    public Object removeObject(Object key) {
        releaseLock(key);
        return null;
    }

    /**
     * 获取锁
     *
     * @param key key
     */
    private void acquireLock(Object key) {
        CountDownLatch newLatch = new CountDownLatch(1);
        while (true) {
            CountDownLatch latch = locks.putIfAbsent(key, newLatch);
            if (latch == null) {
                break;
            }

            try {
                if (timeout > 0) {
                    boolean acquired = latch.await(timeout, TimeUnit.MILLISECONDS);
                    if (!acquired) {
                        throw new CacheException("Couldn't get a lock in " + timeout
                                + " for the key " + key + " at the cache " + delegate.getId());
                    }
                } else {
                    latch.await();
                }
            } catch (InterruptedException e) {
                throw new CacheException("Got interrupted while trying to acquire lock for key " + key, e);
            }
        }
    }

    /**
     * 释放锁
     *
     * @param key key
     */
    private void releaseLock(Object key) {
        CountDownLatch latch = locks.remove(key);
        if (latch == null) {
            throw new IllegalStateException("Detected an attempt at releasing unacquired lock. This should never happen.");
        }

        latch.countDown();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
