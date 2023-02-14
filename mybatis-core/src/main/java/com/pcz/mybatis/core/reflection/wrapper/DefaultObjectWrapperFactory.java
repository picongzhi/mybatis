package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.ReflectionException;

/**
 * 默认的对象包装工厂
 *
 * @author picongzhi
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new ReflectionException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
