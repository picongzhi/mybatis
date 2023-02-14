package com.pcz.mybatis.core.reflection.factory;

import com.pcz.mybatis.core.reflection.ReflectionException;
import com.pcz.mybatis.core.reflection.Reflector;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认的对象工厂
 *
 * @author picongzhi
 */
public class DefaultObjectFactory implements ObjectFactory {
    @Override
    public <T> T create(Class<T> cls) {
        return create(cls, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> cls, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        Class<?> classToCreate = resolveInterface(cls);
        return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
    }

    @Override
    public <T> boolean isCollection(Class<T> cls) {
        return Collection.class.isAssignableFrom(cls);
    }

    /**
     * 解析接口
     *
     * @param cls 接口 Class 实例
     * @return 创建的类型
     */
    protected Class<?> resolveInterface(Class<?> cls) {
        if (cls == List.class || cls == Collection.class || cls == Iterable.class) {
            return ArrayList.class;
        }

        if (cls == Map.class) {
            return HashMap.class;
        }

        if (cls == SortedSet.class) {
            return TreeSet.class;
        }

        if (cls == Set.class) {
            return HashSet.class;
        }

        return cls;
    }

    /**
     * 实例化类
     *
     * @param cls                 Class 实例
     * @param constructorArgTypes 构造参数类型
     * @param constructorArgs     构造参数
     * @param <T>                 泛型
     * @return 实例
     */
    private <T> T instantiateClass(Class<T> cls, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        try {
            Constructor<T> constructor;
            if (constructorArgTypes == null || constructorArgs == null) {
                // 构造参数为 null，获取无参构造方法
                constructor = cls.getDeclaredConstructor();

                try {
                    // 使用无参构造方法实例化
                    return constructor.newInstance();
                } catch (IllegalAccessException e) {
                    // 没有权限
                    if (Reflector.canControlMemberAccessible()) {
                        // 设置权限
                        constructor.setAccessible(true);
                        // 重新实例化
                        return constructor.newInstance();
                    } else {
                        // 不能设置权限，抛出异常
                        throw e;
                    }
                }
            }

            // 带参构造方法
            constructor = cls.getDeclaredConstructor(constructorArgTypes.toArray(new Class[0]));
            try {
                // 使用带参构造方法实例化
                return constructor.newInstance(constructorArgs.toArray(new Object[0]));
            } catch (IllegalAccessException e) {
                // 没有权限
                if (Reflector.canControlMemberAccessible()) {
                    // 设置权限
                    constructor.setAccessible(true);
                    // 重新实例化
                    return constructor.newInstance(constructorArgs.toArray(new Object[0]));
                } else {
                    // 不能设置权限，抛出异常
                    throw e;
                }
            }
        } catch (Exception e) {
            String argTypes = Optional.ofNullable(constructorArgTypes)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(","));
            String argValues = Optional.ofNullable(constructorArgs)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            throw new ReflectionException("Error instantiating " + cls + " with invalid types (" + argTypes
                    + ") or values (" + argValues + "). Cause: " + e, e);
        }
    }
}
