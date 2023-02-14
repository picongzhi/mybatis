package com.pcz.mybatis.core.reflection.invoker;

import com.pcz.mybatis.core.reflection.Reflector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * set 字段调用器
 *
 * @author picongzhi
 */
public class SetFieldInvoker implements Invoker {
    /**
     * 字段
     */
    private final Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        try {
            field.set(target, args[0]);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                field.set(target, args[0]);
            } else {
                throw e;
            }
        }
        
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
