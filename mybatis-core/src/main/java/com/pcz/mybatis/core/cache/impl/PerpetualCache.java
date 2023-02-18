package com.pcz.mybatis.core.cache.impl;

import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.cache.CacheException;

import java.util.HashMap;
import java.util.Map;

/**
 * 永久缓存
 *
 * @author picongzhi
 */
public class PerpetualCache implements Cache {
    /**
     * id
     */
    private final String id;

    /**
     * 缓存
     */
    private final Map<Object, Object> cache = new HashMap<>();

    public PerpetualCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return cache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public boolean equals(Object o) {
        if (getId() == null) {
            throw new CacheException("Cache instances require an ID");
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof Cache)) {
            return false;
        }

        Cache cache = (Cache) o;

        return getId().equals(cache.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            throw new CacheException("Cache instances require an ID");
        }

        return getId().hashCode();
    }
}
