package com.pcz.mybatis.core.executor.loader;

import java.util.Properties;

/**
 * 代理工厂
 *
 * @author picongzhi
 */
public interface ProxyFactory {
    /**
     * 设置属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
    }
}
