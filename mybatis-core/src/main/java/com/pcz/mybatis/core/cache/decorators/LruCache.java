package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU 缓存装饰器
 *
 * @author picongzhi
 */
public class LruCache implements Cache {
    /**
     * 委托缓存
     */
    private final Cache delegate;

    /**
     * LRU Map
     */
    private Map<Object, Object> keyMap;

    /**
     * 最老的 key
     */
    private Object eldestKey;

    public LruCache(Cache delegate) {
        this.delegate = delegate;
        setSize(1024);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(key, value);
        cycleKeyList(key);
    }

    @Override
    public Object getObject(Object key) {
        keyMap.get(key);
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyMap.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    /**
     * 设置缓存大小
     *
     * @param size 缓存大小
     */
    public void setSize(final int size) {
        keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                boolean tooBig = size() > size;
                if (tooBig) {
                    eldestKey = eldest.getKey();
                }

                return tooBig;
            }
        };
    }

    /**
     * 存换 keyMap
     *
     * @param key key
     */
    private void cycleKeyList(Object key) {
        keyMap.put(key, key);
        if (eldestKey != null) {
            delegate.removeObject(eldestKey);
            eldestKey = null;
        }
    }
}
