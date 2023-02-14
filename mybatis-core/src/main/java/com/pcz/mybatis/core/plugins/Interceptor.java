package com.pcz.mybatis.core.plugins;

import java.util.Properties;

/**
 * 拦截器
 *
 * @author picongzhi
 */
public interface Interceptor {
    /**
     * 拦截
     *
     * @param invocation 调用目标
     * @return 结果
     * @throws Throwable 异常
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 获取插件
     *
     * @param target 目标对象
     * @return 插件
     */
    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
    }
}
