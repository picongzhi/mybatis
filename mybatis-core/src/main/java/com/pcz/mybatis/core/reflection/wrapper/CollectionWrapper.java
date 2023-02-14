package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;

import java.util.Collection;
import java.util.List;

/**
 * 集合包装
 *
 * @author picongzhi
 */
public class CollectionWrapper implements ObjectWrapper {
    /**
     * 集合
     */
    private final Collection<Object> collection;

    public CollectionWrapper(MetaObject metaObject, Collection<Object> collection) {
        this.collection = collection;
    }

    @Override
    public Object get(PropertyTokenizer propertyTokenizer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(PropertyTokenizer propertyTokenizer, Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getGetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSetterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer propertyTokenizer, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void add(Object element) {
        collection.add(element);
    }

    @Override
    public <E> void addAll(List<E> element) {
        collection.addAll(element);
    }
}
