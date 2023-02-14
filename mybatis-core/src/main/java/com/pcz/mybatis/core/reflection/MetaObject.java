package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;
import com.pcz.mybatis.core.reflection.wrapper.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 对象元信息
 *
 * @author picongzhi
 */
public class MetaObject {
    /**
     * 原始对象
     */
    private final Object originalObject;

    /**
     * 包装对象
     */
    private final ObjectWrapper objectWrapper;

    /**
     * 对象工厂
     */
    private final ObjectFactory objectFactory;

    /**
     * 包装对象工厂
     */
    private final ObjectWrapperFactory objectWrapperFactory;

    /**
     * 反射工厂
     */
    private final ReflectorFactory reflectorFactory;

    public MetaObject(Object object,
                      ObjectFactory objectFactory,
                      ObjectWrapperFactory objectWrapperFactory,
                      ReflectorFactory reflectorFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;
        this.reflectorFactory = reflectorFactory;

        if (object instanceof ObjectWrapper) {
            this.objectWrapper = (ObjectWrapper) object;
        } else if (objectWrapperFactory.hasWrapperFor(object)) {
            this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
        } else if (object instanceof Map) {
            this.objectWrapper = new MapWrapper(this, (Map) object);
        } else if (object instanceof Collection) {
            this.objectWrapper = new CollectionWrapper(this, (Collection) object);
        } else {
            this.objectWrapper = new BeanWrapper(this, object);
        }
    }

    /**
     * 获取对象元信息
     *
     * @param object               对象
     * @param objectFactory        对象工厂
     * @param objectWrapperFactory 对象包装工厂
     * @param reflectorFactory     反射工厂
     * @return 对象元信息
     */
    public static MetaObject forObject(Object object,
                                       ObjectFactory objectFactory,
                                       ObjectWrapperFactory objectWrapperFactory,
                                       ReflectorFactory reflectorFactory) {
        if (object == null) {
            return SystemMetaObject.NULL_META_OBJECT;
        } else {
            return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
        }
    }

    /**
     * 获取原始对象
     *
     * @return 原始对象
     */
    public Object getOriginalObject() {
        return originalObject;
    }

    /**
     * 获取包装对象
     *
     * @return 包装对象
     */
    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }

    /**
     * 获取对象工厂
     *
     * @return 对象工厂
     */
    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    /**
     * 获取包装对象工厂
     *
     * @return 包装对象工厂
     */
    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    /**
     * 获取反射器工厂
     *
     * @return 反射器工厂
     */
    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    /**
     * 获取属性
     *
     * @param propertyName        属性名
     * @param useCamelCaseMapping 是否使用驼峰映射
     * @return 属性
     */
    public String findProperty(String propertyName, boolean useCamelCaseMapping) {
        return objectWrapper.findProperty(propertyName, useCamelCaseMapping);
    }

    /**
     * 获取 getter 名
     *
     * @return getter 名
     */
    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    /**
     * 获取 setter 名
     *
     * @return setter 名
     */
    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    /**
     * 判断是否有 getter
     *
     * @param name 属性名
     * @return 是否有 getter
     */
    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }

    /**
     * 判断是否有 setter
     *
     * @param name 属性名
     * @return 是否有 setter
     */
    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    /**
     * 获取属性值
     *
     * @param name 属性名
     * @return 属性值
     */
    public Object getValue(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaObject propertyMetaObject = metaObjectForProperty(propertyTokenizer.getIndexedName());
            if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                return null;
            } else {
                return propertyMetaObject.getValue(propertyTokenizer.getChildren());
            }
        } else {
            return objectWrapper.get(propertyTokenizer);
        }
    }

    /**
     * 设置属性值
     *
     * @param name  属性名
     * @param value 属性值
     */
    public void setValue(String name, Object value) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaObject propertyMetaObject = metaObjectForProperty(propertyTokenizer.getIndexedName());
            if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                if (value == null) {
                    return;
                } else {
                    propertyMetaObject = objectWrapper.instantiatePropertyValue(name, propertyTokenizer, objectFactory);
                }
            }
            propertyMetaObject.setValue(propertyTokenizer.getChildren(), value);
        } else {
            objectWrapper.set(propertyTokenizer, value);
        }
    }

    /**
     * 获取属性对象元信息
     *
     * @param name 属性名
     * @return 属性对象元信息
     */
    public MetaObject metaObjectForProperty(String name) {
        Object value = getValue(name);
        return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    /**
     * 获取 getter 类型
     *
     * @param name 属性名
     * @return getter 类型
     */
    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    /**
     * 获取 setter 类型
     *
     * @param name 属性名
     * @return setter 类型
     */
    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    /**
     * 判断是否是集合
     *
     * @return 是否是集合
     */
    public boolean isCollection() {
        return objectWrapper.isCollection();
    }

    /**
     * 添加元素
     *
     * @param element 元素
     */
    public void add(Object element) {
        objectWrapper.add(element);
    }

    /**
     * 添加元素列表
     *
     * @param list 元素列表
     * @param <E>  元素泛型
     */
    public <E> void addAll(List<E> list) {
        objectWrapper.addAll(list);
    }
}
