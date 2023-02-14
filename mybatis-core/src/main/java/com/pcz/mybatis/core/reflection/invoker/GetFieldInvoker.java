package com.pcz.mybatis.core.reflection.invoker;

import com.pcz.mybatis.core.reflection.Reflector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * get 字段调用器
 *
 * @author picongzhi
 */
public class GetFieldInvoker implements Invoker {
    /**
     * 字段
     */
    private final Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                return field.get(target);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
