package com.pcz.mybatis.core.plugins;

import com.pcz.mybatis.core.util.MapUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 插件
 *
 * @author picongzhi
 */
public class Plugin implements InvocationHandler {
    /**
     * 目标对象
     */
    private final Object target;

    /**
     * 拦截器
     */
    private final Interceptor interceptor;

    /**
     * 类实例 -> 方法 映射
     */
    private final Map<Class<?>, Set<Method>> signatureMap;

    public Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    /**
     * 包装目标对象和拦截器，返回插件
     *
     * @param target      目标对象
     * @param interceptor 拦截器
     * @return 插件
     */
    public static Object wrap(Object target, Interceptor interceptor) {
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);

        Class<?> targetClass = target.getClass();
        Class<?>[] targetInterfaces = getAllInterfaces(targetClass, signatureMap);

        return targetInterfaces.length > 0
                ? Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetInterfaces,
                new Plugin(target, interceptor, signatureMap))
                : target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

    /**
     * 根据拦截器获取关联的 类实例 -> 方法 映射
     *
     * @param interceptor 拦截器
     * @return 类实例 -> 方法 映射
     */
    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
        Intercepts interceptsAnnotation = interceptor.getClass()
                .getAnnotation(Intercepts.class);
        if (interceptsAnnotation == null) {
            throw new PluginException("No @Intercepts annotation was found in interceptor "
                    + interceptor.getClass().getName());
        }

        Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();

        Signature[] signatures = interceptsAnnotation.value();
        for (Signature signature : signatures) {
            Set<Method> methods = MapUtil.compoteIfAbsent(signatureMap,
                    signature.type(), k -> new HashSet<>());
            try {
                Method method = signature.type()
                        .getMethod(signature.method(), signature.args());
                methods.add(method);
            } catch (NoSuchMethodException e) {
                throw new PluginException("Could not find method on " + signature.type()
                        + " named " + signature.method() + ". Cause: " + e, e);
            }
        }

        return signatureMap;
    }

    private static Class<?>[] getAllInterfaces(Class<?> cls, Map<Class<?>, Set<Method>> signatureMap) {
        Set<Class<?>> interfaces = new HashSet<>();
        while (cls != null) {
            for (Class<?> interfaceClass : cls.getInterfaces()) {
                if (signatureMap.containsKey(interfaceClass)) {
                    interfaces.add(interfaceClass);
                }
            }
            cls = cls.getSuperclass();
        }

        return interfaces.toArray(new Class<?>[0]);
    }
}
