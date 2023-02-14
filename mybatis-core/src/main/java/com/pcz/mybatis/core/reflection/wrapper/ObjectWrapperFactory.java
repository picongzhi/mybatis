package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.MetaObject;

/**
 * 对象包装工厂
 *
 * @author picongzhi
 */
public interface ObjectWrapperFactory {
    /**
     * 判断是否有包装对象
     *
     * @param object 对象
     * @return 是否有包装对象
     */
    boolean hasWrapperFor(Object object);

    /**
     * 获取对象包装
     *
     * @param metaObject 对象元信息
     * @param object     对象
     * @return 对象包装
     */
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);
}
