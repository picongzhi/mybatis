package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.util.MapUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的反射器工厂
 *
 * @author picongzhi
 */
public class DefaultReflectorFactory implements ReflectorFactory {
    /**
     * 是否开启 Class 缓存
     */
    private boolean classCacheEnabled = true;

    /**
     * 反射器映射
     */
    private final ConcurrentHashMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    @Override
    public boolean isClassCacheEnabled() {
        return classCacheEnabled;
    }

    @Override
    public void setClassCacheEnabled(boolean classCacheEnabled) {
        this.classCacheEnabled = classCacheEnabled;
    }

    @Override
    public Reflector findForClass(Class<?> cls) {
        if (classCacheEnabled) {
            return MapUtil.compoteIfAbsent(reflectorMap, cls, Reflector::new);
        } else {
            return new Reflector(cls);
        }
    }
}
