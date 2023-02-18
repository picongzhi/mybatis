package com.pcz.mybatis.core.mapping;

import com.pcz.mybatis.core.builder.InitializingObject;
import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.cache.CacheException;
import com.pcz.mybatis.core.cache.decorators.*;
import com.pcz.mybatis.core.cache.impl.PerpetualCache;
import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.SystemMetaObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 缓存构造器
 *
 * @author picongzhi
 */
public class CacheBuilder {
    /**
     * id
     */
    private final String id;

    /**
     * 实现类
     */
    private Class<? extends Cache> implementation;

    /**
     * 装饰器
     */
    private final List<Class<? extends Cache>> decorators;

    /**
     * 缓存数量
     */
    private Integer size;

    /**
     * 清理间隔
     */
    private Long clearInterval;

    /**
     * 读写
     */
    private boolean readWrite;

    /**
     * 属性
     */
    private Properties properties;

    /**
     * 是否阻塞
     */
    private boolean blocking;

    public CacheBuilder(String id) {
        this.id = id;
        this.decorators = new ArrayList<>();
    }

    public CacheBuilder implementation(Class<? extends Cache> implementation) {
        this.implementation = implementation;
        return this;
    }

    public CacheBuilder addDecorator(Class<? extends Cache> decorator) {
        if (decorator != null) {
            this.decorators.add(decorator);
        }
        return this;
    }

    public CacheBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    public CacheBuilder clearInterval(Long clearInterval) {
        this.clearInterval = clearInterval;
        return this;
    }

    public CacheBuilder readWrite(boolean readWrite) {
        this.readWrite = readWrite;
        return this;
    }

    public CacheBuilder blocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    public CacheBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * 构造
     *
     * @return 缓存
     */
    public Cache build() {
        setDefaultImplementations();
        Cache cache = newBaseCacheInstance(implementation, id);
        setCacheproperties(cache);

        if (PerpetualCache.class.equals(cache.getClass())) {
            for (Class<? extends Cache> decorator : decorators) {
                cache = newCacheDecoratorInstance(decorator, cache);
                setCacheproperties(cache);
            }

            cache = setStandardDecorators(cache);
        } else if (!LoggingCache.class.isAssignableFrom(cache.getClass())) {
            cache = new LoggingCache(cache);
        }

        return cache;
    }

    /**
     * 设置默认的实现
     */
    private void setDefaultImplementations() {
        if (implementation == null) {
            implementation = PerpetualCache.class;
            if (decorators.isEmpty()) {
                decorators.add(LruCache.class);
            }
        }
    }


    /**
     * 实例化根缓存实例
     *
     * @param cacheClass 缓存 Class 实例
     * @param id         id
     * @return 根缓存实例
     */
    private Cache newBaseCacheInstance(Class<? extends Cache> cacheClass, String id) {
        Constructor<? extends Cache> cacheConstructor = getBaseCacheConstructor(cacheClass);

        try {
            return cacheConstructor.newInstance(id);
        } catch (Exception e) {
            throw new CacheException("Could not instantiate cache implementation ("
                    + cacheClass + "). Cause: " + e, e);
        }
    }

    /**
     * 获取根缓存构造器
     *
     * @param cacheClass 缓存 Class 实例
     * @return 构造器
     */
    private Constructor<? extends Cache> getBaseCacheConstructor(Class<? extends Cache> cacheClass) {
        try {
            return cacheClass.getConstructor(String.class);
        } catch (Exception e) {
            throw new CacheException("Invalid base cache implementation (" + cacheClass + "). "
                    + "Base cache implementations must have a constructor that takes a String id as a parameter. Cause: " + e, e);
        }
    }

    /**
     * 设置缓存属性
     *
     * @param cache 缓存
     */
    private void setCacheproperties(Cache cache) {
        if (properties != null) {
            MetaObject metaObject = SystemMetaObject.forObject(cache);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (!metaObject.hasSetter(name)) {
                    continue;
                }

                Class<?> type = metaObject.getSetterType(name);
                if (String.class == type) {
                    metaObject.setValue(name, value);
                } else if (int.class == type
                        || Integer.class == type) {
                    metaObject.setValue(name, Integer.valueOf(value));
                } else if (long.class == type
                        || Long.class == type) {
                    metaObject.setValue(name, Long.valueOf(value));
                } else if (short.class == type
                        || Short.class == type) {
                    metaObject.setValue(name, Short.valueOf(value));
                } else if (byte.class == type
                        || Byte.class == type) {
                    metaObject.setValue(name, Byte.valueOf(value));
                } else if (float.class == type
                        || Float.class == type) {
                    metaObject.setValue(name, Float.valueOf(value));
                } else if (boolean.class == type
                        || Boolean.class == type) {
                    metaObject.setValue(name, Boolean.valueOf(value));
                } else if (double.class == type
                        || Double.class == type) {
                    metaObject.setValue(name, Double.valueOf(value));
                } else {
                    throw new CacheException("Unsupported property type for cache: '" + name + "' of type " + type);
                }
            }
        }

        if (InitializingObject.class.isAssignableFrom(cache.getClass())) {
            try {
                ((InitializingObject) cache).initialize();
            } catch (Exception e) {
                throw new CacheException("Failed cache initialization for '"
                        + cache.getId() + "' on '" + cache.getClass().getName() + "'", e);
            }
        }
    }

    /**
     * 实例化缓存装饰器
     *
     * @param cacheDecoratorClass 缓存装饰器 Class 实例
     * @param baseCache           根缓存
     * @return 缓存装饰器
     */
    private Cache newCacheDecoratorInstance(Class<? extends Cache> cacheDecoratorClass, Cache baseCache) {
        Constructor<? extends Cache> cacheDecoratorConstructor = getCacheDecoratorConstructor(cacheDecoratorClass);

        try {
            return cacheDecoratorConstructor.newInstance(baseCache);
        } catch (Exception e) {
            throw new CacheException("Invalid cache decorator (" + cacheDecoratorClass + "). "
                    + "Cache decorators must have a constructor that takes a Cache instance as a parameter. Cause: " + e, e);
        }
    }

    /**
     * 获取缓存装饰器构造器
     *
     * @param cacheDecoratorClass 缓存装饰器 Class 实例
     * @return 缓存装饰器构造器
     */
    private Constructor<? extends Cache> getCacheDecoratorConstructor(Class<? extends Cache> cacheDecoratorClass) {
        try {
            return cacheDecoratorClass.getConstructor(Cache.class);
        } catch (Exception e) {
            throw new CacheException("Invalid cache decorator (" + cacheDecoratorClass + "). "
                    + "Cache decorators must have a constructor that takes a Cache instance as a parameter. Cause: " + e, e);
        }
    }

    /**
     * 设置标准的装饰器
     *
     * @param cache 缓存
     * @return 装饰后的缓存
     */
    private Cache setStandardDecorators(Cache cache) {
        try {
            MetaObject metaObject = SystemMetaObject.forObject(cache);
            if (size != null && metaObject.hasSetter("size")) {
                metaObject.setValue("size", size);
            }

            if (clearInterval != null) {
                cache = new ScheduledCache(cache);
                ((ScheduledCache) cache).setClearInterval(clearInterval);
            }

            if (readWrite) {
                cache = new SerializedCache(cache);
            }

            cache = new LoggingCache(cache);

            cache = new SynchronizedCache(cache);

            if (blocking) {
                cache = new BlockingCache(cache);
            }

            return cache;
        } catch (Exception e) {
            throw new CacheException("Error building standard cache decorators. Cause: " + e, e);
        }
    }
}
