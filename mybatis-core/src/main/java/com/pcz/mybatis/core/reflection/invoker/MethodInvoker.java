package com.pcz.mybatis.core.reflection.invoker;

import com.pcz.mybatis.core.reflection.Reflector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法调用器
 *
 * @author picongzhi
 */
public class MethodInvoker implements Invoker {
    /**
     * 类型
     */
    private final Class<?> type;

    /**
     * 方法
     */
    private final Method method;

    public MethodInvoker(Method method) {
        this.method = method;

        if (method.getParameterTypes().length == 1) {
            // 如果方法参数只有一个，取方法参数类型
            type = method.getParameterTypes()[0];
        } else {
            // 如果方法参数个数不是一个，取返回值类型
            type = method.getReturnType();
        }
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            // 没有方法权限
            if (Reflector.canControlMemberAccessible()) {
                // 设置权限并重新调用
                method.setAccessible(true);
                return method.invoke(target, args);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
