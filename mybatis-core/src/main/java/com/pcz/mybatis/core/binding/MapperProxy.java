package com.pcz.mybatis.core.binding;

import com.pcz.mybatis.core.reflection.ExceptionUtil;
import com.pcz.mybatis.core.session.SqlSession;
import com.pcz.mybatis.core.util.MapUtil;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Mapper 代理
 *
 * @param <T> Mapper 泛型类
 * @author picongzhi
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {
    /**
     * 允许的模式
     */
    private static final int ALLOWED_MODES =
            MethodHandles.Lookup.PRIVATE
                    | MethodHandles.Lookup.PROTECTED
                    | MethodHandles.Lookup.PACKAGE
                    | MethodHandles.Lookup.PUBLIC;

    /**
     * privateLookupIn 方法
     */
    private static final Method privateLookupInMethod;

    /**
     * lookup 构造器
     */
    private static final Constructor<MethodHandles.Lookup> lookupConstructor;

    /**
     * Sql 会话
     */
    private final SqlSession sqlSession;

    /**
     * Mapper 接口实例
     */
    private final Class<T> mapperInterface;

    /**
     * 方法缓存
     */
    private final Map<Method, MapperMethodInvoker> methodCache;

    static {
        // privateLookupInMethod
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }

        privateLookupInMethod = privateLookupIn;

        // lookupConstructor
        Constructor<MethodHandles.Lookup> lookup = null;
        if (privateLookupInMethod == null) {
            try {
                lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("There is neither 'privateLookupIn(Class, Lookup)' " +
                        "nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.", e);
            } catch (Exception e) {
                lookup = null;
            }
        }

        lookupConstructor = lookup;
    }

    public MapperProxy(SqlSession sqlSession,
                       Class<T> mapperInterface,
                       Map<Method, MapperMethodInvoker> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                // Object 中的方法
                return method.invoke(this, args);
            } else {
                return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * 获取缓存的调用器
     *
     * @param method 方法
     * @return Mapper 方法调用器
     * @throws Throwable 异常
     */
    private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
        try {
            return MapUtil.compoteIfAbsent(methodCache, method, k -> {
                if (k.isDefault()) {
                    // default 方法
                    try {
                        if (privateLookupInMethod == null) {
                            return new DefaultMethodInvoker(getMethodHandleJava8(method));
                        } else {
                            return new DefaultMethodInvoker(getMethodHandleJava9(method));
                        }
                    } catch (IllegalAccessException | InstantiationException
                            | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // 其他 interface 方法
                    return new PlainMethodInvoker(
                            new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()));
                }
            });
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            throw cause == null ? e : cause;
        }
    }

    /**
     * Java8 获取 MethodHandle
     *
     * @param method 方法
     * @return MethodHandle
     * @throws IllegalAccessException    非法访问异常
     * @throws InstantiationException    实例化异常
     * @throws InvocationTargetException 执行目标异常
     */
    private MethodHandle getMethodHandleJava8(Method method)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
    }

    /**
     * Java9 获取 MethodHandle
     *
     * @param method 方法
     * @return MethodHandle
     * @throws NoSuchMethodException     没有方法异常
     * @throws IllegalAccessException    非法访问异常
     * @throws InvocationTargetException 执行目标异常
     */
    private MethodHandle getMethodHandleJava9(Method method)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) privateLookupInMethod.invoke(
                null, declaringClass, MethodHandles.lookup()))
                .findSpecial(declaringClass,
                        method.getName(),
                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                        declaringClass);
    }

    /**
     * Mapper 方法调用器
     */
    interface MapperMethodInvoker {
        /**
         * 执行调用
         *
         * @param proxy      代理对象
         * @param method     方法
         * @param args       参数
         * @param sqlSession Sql 会话
         * @return 执行结果
         * @throws Throwable 执行异常
         */
        Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
    }

    /**
     * 普通的 Mapper 方法调用器
     */
    private static class PlainMethodInvoker implements MapperMethodInvoker {
        /**
         * Mapper 方法
         */
        private final MapperMethod mapperMethod;

        public PlainMethodInvoker(MapperMethod mapperMethod) {
            super();
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return mapperMethod.execute(sqlSession, args);
        }
    }

    /**
     * 默认的 Mapper 方法调用器
     */
    private static class DefaultMethodInvoker implements MapperMethodInvoker {
        /**
         * MethodHandle
         */
        private final MethodHandle methodHandle;

        public DefaultMethodInvoker(MethodHandle methodHandle) {
            super();
            this.methodHandle = methodHandle;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        }
    }
}
