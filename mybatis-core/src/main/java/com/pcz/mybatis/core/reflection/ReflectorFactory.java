package com.pcz.mybatis.core.reflection;

/**
 * 反射器工厂
 *
 * @author picongzhi
 */
public interface ReflectorFactory {
    /**
     * 判断 Class 缓存是否开启
     *
     * @return Class 缓存是否开启
     */
    boolean isClassCacheEnabled();

    /**
     * 设置 Class 缓存是否开启
     *
     * @param classCacheEnabled Class 缓存是否开启
     */
    void setClassCacheEnabled(boolean classCacheEnabled);

    /**
     * 获取反射器
     *
     * @param cls Class 实例
     * @return 反射器
     */
    Reflector findForClass(Class<?> cls);
}
