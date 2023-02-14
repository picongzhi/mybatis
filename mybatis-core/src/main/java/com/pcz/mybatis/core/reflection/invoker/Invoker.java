package com.pcz.mybatis.core.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * 调用器
 *
 * @author picongzhi
 */
public interface Invoker {
    /**
     * 调用方法
     *
     * @param target 目标对象
     * @param args   参数
     * @return 返回结果
     * @throws IllegalAccessException    非法权限异常
     * @throws InvocationTargetException 执行目标异常
     */
    Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

    /**
     * 获取调用的目标类型
     *
     * @return 调用的目标类型
     */
    Class<?> getType();
}
