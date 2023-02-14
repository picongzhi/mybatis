package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.*;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.invoker.Invoker;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * Bean 包装
 *
 * @author picongzhi
 */
public class BeanWrapper extends BaseWrapper {
    /**
     * 对象
     */
    private final Object object;

    /**
     * Class 元信息
     */
    private final MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject, Object object) {
        super(metaObject);
        this.object = object;
        this.metaClass = MetaClass.forClass(object.getClass(), metaObject.getReflectorFactory());
    }

    @Override
    public Object get(PropertyTokenizer propertyTokenizer) {
        if (propertyTokenizer.getIndex() != null) {
            Object collection = resolveCollection(propertyTokenizer, object);
            return getCollectionValue(propertyTokenizer, collection);
        } else {
            return getBeanProperty(propertyTokenizer, object);
        }
    }

    @Override
    public void set(PropertyTokenizer propertyTokenizer, Object value) {
        if (propertyTokenizer.getIndex() != null) {
            Object collection = resolveCollection(propertyTokenizer, object);
            setCollectionValue(propertyTokenizer, collection, value);
        } else {
            setBeanProperty(propertyTokenizer, object, value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return metaClass.findProperty(name, useCamelCaseMapping);
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
            if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getGetterType(name);
            } else {
                return propertyMetaObject.getGetterType(propertyTokenizer.getChildren());
            }
        } else {
            return metaClass.getGetterType(name);
        }
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
            if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getSetterType(name);
            } else {
                return propertyMetaObject.getSetterType(propertyTokenizer.getChildren());
            }
        } else {
            return metaClass.getSetterType(name);
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            if (metaClass.hasGetter(propertyTokenizer.getIndexedName())) {
                MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
                if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasSetter(name);
                } else {
                    return propertyMetaObject.hasGetter(propertyTokenizer.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasGetter(name);
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            if (metaClass.hasSetter(propertyTokenizer.getIndexedName())) {
                MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
                if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasSetter(name);
                } else {
                    return propertyMetaObject.hasSetter(propertyTokenizer.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasSetter(name);
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer propertyTokenizer, ObjectFactory objectFactory) {
        MetaObject propertyMetaObject;
        Class<?> type = getSetterType(propertyTokenizer.getName());
        try {
            Object object = objectFactory.create(type);
            propertyMetaObject = MetaObject.forObject(object,
                    metaObject.getObjectFactory(),
                    metaObject.getObjectWrapperFactory(),
                    metaObject.getReflectorFactory());
            set(propertyTokenizer, object);
        } catch (Exception e) {
            throw new ReflectionException("Cannot set value of property '" + name
                    + "' because '" + name + "' is null and cannot be instantiated on instance of "
                    + type.getName() + ". Cause: " + e.toString(), e);
        }

        return propertyMetaObject;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取 Bean 属性
     *
     * @param propertyTokenizer 属性分词器
     * @param object            Bean 对象
     * @return 属性
     */
    private Object getBeanProperty(PropertyTokenizer propertyTokenizer, Object object) {
        try {
            Invoker invoker = metaClass.getGetInvoker(propertyTokenizer.getName());
            try {
                return invoker.invoke(object, NO_ARGUMENTS);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new ReflectionException("Could not get property '" + propertyTokenizer.getName()
                    + "' from " + object.getClass() + ". Cause: " + t.toString(), t);
        }
    }

    /**
     * 设置 Bean 属性
     *
     * @param propertyTokenizer 属性分词器
     * @param object            Bean 对象
     * @param value             属性值
     */
    private void setBeanProperty(PropertyTokenizer propertyTokenizer, Object object, Object value) {
        try {
            Invoker invoker = metaClass.getSetInvoker(propertyTokenizer.getName());
            Object[] params = {value};
            try {
                invoker.invoke(object, params);
            } catch (Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        } catch (Throwable t) {
            throw new ReflectionException("Could not set property '" + propertyTokenizer.getName()
                    + "' of '" + object.getClass() + "' with value '" + value + "' Cause: " + t.toString(), t);
        }
    }
}
