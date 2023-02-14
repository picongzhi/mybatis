package com.pcz.mybatis.core.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * 缓存
 *
 * @author picongzhi
 */
public interface Cache {
    /**
     * 获取缓存的标识
     *
     * @return 缓存的标识
     */
    String getId();

    /**
     * 缓存对象
     *
     * @param key   key
     * @param value value
     */
    void putObject(Object key, Object value);

    /**
     * 获取缓存的对象
     *
     * @param key key
     * @return 缓存的对象
     */
    Object getObject(Object key);

    /**
     * 移除缓存的对象
     *
     * @param key key
     * @return 缓存的对象
     */
    Object removeObject(Object key);

    /**
     * 清理缓存
     */
    void clear();

    /**
     * 获取缓存数
     *
     * @return 缓存数
     */
    int getSize();

    /**
     * 获取读写锁
     *
     * @return 读写锁
     */
    default ReadWriteLock getReadWriteLock() {
        return null;
    }
}
