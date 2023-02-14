package com.pcz.mybatis.core.reflection;

import java.lang.reflect.*;

/**
 * 类型参数解析器
 *
 * @author picongzhi
 */
public class TypeParameterResolver {
    /**
     * 解析方法返回类型
     *
     * @param method     方法
     * @param sourceType 来源类型
     * @return 返回类型
     */
    public static Type resolveReturnType(Method method, Type sourceType) {
        // 方法返回类型
        Type returnType = method.getGenericReturnType();

        // 声明方法的类
        Class<?> declaringClass = method.getDeclaringClass();

        // 解析
        return resolveType(returnType, sourceType, declaringClass);
    }

    /**
     * 解析方法参数类型
     *
     * @param method 方法
     * @return 参数类型
     */
    public static Type[] resolveParamTypes(Method method) {
        return method.getGenericParameterTypes();
    }

    /**
     * 解析字段类型
     *
     * @param field      字段
     * @param sourceType 来源类型
     * @return 字段类型
     */
    public static Type resolveFieldType(Field field, Type sourceType) {
        // 字段类型
        Type fieldType = field.getGenericType();

        // 声明字段的类
        Class<?> declaringClass = field.getDeclaringClass();

        // 解析
        return resolveType(fieldType, sourceType, declaringClass);
    }

    /**
     * 解析类型
     *
     * @param type           类型
     * @param sourceType     类型来源
     * @param declaringClass 声明类型的类
     * @return 解析结果
     */
    private static Type resolveType(Type type, Type sourceType, Class<?> declaringClass) {
//        if (type instanceof TypeVariable) {
//            return resolveTypeValirable((TypeVariable<?>) type, sourceType, declaringClass);
//        }
//
//        if (type instanceof ParameterizedType) {
//            return resolveParameterizedType((ParameterizedType) type, sourceType, declaringClass);
//        }
//
//        if (type instanceof GenericArrayType) {
//            return resolveGenericArrayType((GenericArrayType) type, sourceType, declaringClass);
//        }

        return type;
    }
}
