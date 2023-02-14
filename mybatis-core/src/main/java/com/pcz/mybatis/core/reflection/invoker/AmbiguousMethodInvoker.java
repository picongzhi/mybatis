package com.pcz.mybatis.core.reflection.invoker;

import com.pcz.mybatis.core.reflection.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 模糊的方法调用器
 *
 * @author picongzhi
 */
public class AmbiguousMethodInvoker extends MethodInvoker {
    /**
     * 异常信息
     */
    private final String exceptionMessage;

    public AmbiguousMethodInvoker(Method method, String exceptionMessage) {
        super(method);
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        throw new ReflectionException(exceptionMessage);
    }
}
