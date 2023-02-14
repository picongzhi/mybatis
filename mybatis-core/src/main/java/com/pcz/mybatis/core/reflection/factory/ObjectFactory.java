package com.pcz.mybatis.core.reflection.factory;

import java.util.List;
import java.util.Properties;

/**
 * 对象工厂
 *
 * @author picongzhi
 */
public interface ObjectFactory {
    /**
     * 设置属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
    }

    /**
     * 创建对象
     *
     * @param cls 对象 Class 实例
     * @param <T> 泛型
     * @return 对象实例
     */
    <T> T create(Class<T> cls);

    /**
     * 创建对象
     *
     * @param cls                 对象 Class 实例
     * @param constructorArgTypes 构造参数类型
     * @param constructorArgs     构造参数
     * @param <T>                 泛型
     * @return 实例对象
     */
    <T> T create(Class<T> cls, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

    /**
     * 判断是否是集合
     *
     * @param cls 对象 Class 实例
     * @param <T> 泛型
     * @return 是否是集合
     */
    <T> boolean isCollection(Class<T> cls);
}
