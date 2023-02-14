package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.ReflectionException;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;

import java.util.List;
import java.util.Map;

/**
 * 基本对象 Wrapper
 *
 * @author picongzhi
 */
public abstract class BaseWrapper implements ObjectWrapper {
    /**
     * 空参
     */
    protected static final Object[] NO_ARGUMENTS = new Object[0];

    /**
     * 元信息对象
     */
    protected final MetaObject metaObject;

    protected BaseWrapper(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    /**
     * 解析集合
     *
     * @param propertyTokenizer 属性分词器
     * @param object            值
     * @return 集合
     */
    protected Object resolveCollection(PropertyTokenizer propertyTokenizer, Object object) {
        if ("".equals(propertyTokenizer.getName())) {
            return object;
        } else {
            return metaObject.getValue(propertyTokenizer.getName());
        }
    }

    /**
     * 获取集合值
     *
     * @param propertyTokenizer 属性分词器
     * @param collection        集合
     * @return 集合值
     */
    protected Object getCollectionValue(PropertyTokenizer propertyTokenizer, Object collection) {
        if (collection instanceof Map) {
            return ((Map) collection).get(propertyTokenizer.getIndex());
        }

        int index = Integer.parseInt(propertyTokenizer.getIndex());
        if (collection instanceof List) {
            return ((List) collection).get(index);
        } else if (collection instanceof Object[]) {
            return ((Object[]) collection)[index];
        } else if (collection instanceof char[]) {
            return ((char[]) collection)[index];
        } else if (collection instanceof boolean[]) {
            return ((boolean[]) collection)[index];
        } else if (collection instanceof byte[]) {
            return ((byte[]) collection)[index];
        } else if (collection instanceof double[]) {
            return ((double[]) collection)[index];
        } else if (collection instanceof float[]) {
            return ((float[]) collection)[index];
        } else if (collection instanceof int[]) {
            return ((int[]) collection)[index];
        } else if (collection instanceof long[]) {
            return ((long[]) collection)[index];
        } else if (collection instanceof short[]) {
            return ((short[]) collection)[index];
        }

        throw new ReflectionException("The '" + propertyTokenizer.getName()
                + "' property of " + collection + " is not a List or Array.");
    }

    /**
     * 设置集合值
     *
     * @param propertyTokenizer 属性分词器
     * @param collection        集合
     * @param value             值
     */
    protected void setCollectionValue(PropertyTokenizer propertyTokenizer, Object collection, Object value) {
        if (collection instanceof Map) {
            ((Map) collection).put(propertyTokenizer.getIndex(), value);
            return;
        }

        int index = Integer.parseInt(propertyTokenizer.getIndex());
        if (collection instanceof List) {
            ((List) collection).set(index, value);
        } else if (collection instanceof Object[]) {
            ((Object[]) collection)[index] = value;
        } else if (collection instanceof char[]) {
            ((char[]) collection)[index] = (Character) value;
        } else if (collection instanceof boolean[]) {
            ((boolean[]) collection)[index] = (Boolean) value;
        } else if (collection instanceof byte[]) {
            ((byte[]) collection)[index] = (Byte) value;
        } else if (collection instanceof double[]) {
            ((double[]) collection)[index] = (Double) value;
        } else if (collection instanceof float[]) {
            ((float[]) collection)[index] = (Float) value;
        } else if (collection instanceof int[]) {
            ((int[]) collection)[index] = (Integer) value;
        } else if (collection instanceof long[]) {
            ((long[]) collection)[index] = (Long) value;
        } else if (collection instanceof short[]) {
            ((short[]) collection)[index] = (Short) value;
        }

        throw new ReflectionException("The '" + propertyTokenizer.getName()
                + "' property of " + collection + " is not a List or Array.");
    }
}
