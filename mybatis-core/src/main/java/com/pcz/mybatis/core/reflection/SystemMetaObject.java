package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.factory.DefaultObjectFactory;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.wrapper.DefaultObjectWrapperFactory;
import com.pcz.mybatis.core.reflection.wrapper.ObjectWrapperFactory;

/**
 * 系统元信息对象
 *
 * @author picongzhi
 */
public final class SystemMetaObject {
    /**
     * 默认的对象工厂
     */
    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();

    /**
     * 默认的对象包装工厂
     */
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

    /**
     * 空对象元信息
     */
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(
            new NullObject(), DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

    /**
     * 空对象
     */
    private static class NullObject {
    }

    /**
     * 获取对象元信息
     *
     * @param object 对象
     * @return 对象元信息
     */
    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(
                object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
    }
}
