package com.pcz.mybatis.core.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 调用信息
 *
 * @author picongzhi
 */
public class Invocation {
    /**
     * 目标对象
     */
    private final Object target;

    /**
     * 调用方法
     */
    private final Method method;

    /**
     * 方法参数
     */
    private final Object[] args;

    public Invocation(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    /**
     * 执行方法
     *
     * @return 执行结果
     * @throws InvocationTargetException 调用目标异常
     * @throws IllegalAccessException    非法访问权限异常
     */
    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }
}
