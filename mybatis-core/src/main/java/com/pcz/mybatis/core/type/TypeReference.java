package com.pcz.mybatis.core.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型引用
 *
 * @param <T> 引用泛型
 * @author picongzhi
 */
public abstract class TypeReference<T> {
    /**
     * 原始类型
     */
    private final Type rawType;

    protected TypeReference() {
        rawType = getSuperClassTypeParameter(getClass());
    }

    Type getSuperClassTypeParameter(Class<?> cls) {
        Type genericSuperclass = cls.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            if (TypeReference.class != genericSuperclass) {
                return getSuperClassTypeParameter(cls.getSuperclass());
            }

            throw new TypeException("'" + getClass() + "' extends TypeReference but mises the type parameter. "
                    + " Remove the extension or add a type parameter to it.");
        }

        Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }

        return rawType;
    }

    public final Type getRawType() {
        return rawType;
    }

    @Override
    public String toString() {
        return rawType.toString();
    }
}
