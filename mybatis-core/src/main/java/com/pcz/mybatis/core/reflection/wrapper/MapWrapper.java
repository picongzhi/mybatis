package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.SystemMetaObject;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map 包装
 *
 * @author picongzhi
 */
public class MapWrapper extends BaseWrapper {
    /**
     * map
     */
    private final Map<String, Object> map;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject);
        this.map = map;
    }

    @Override
    public Object get(PropertyTokenizer propertyTokenizer) {
        if (propertyTokenizer.getIndex() != null) {
            Object collection = resolveCollection(propertyTokenizer, map);
            return getCollectionValue(propertyTokenizer, collection);
        } else {
            return map.get(propertyTokenizer.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer propertyTokenizer, Object object) {
        if (propertyTokenizer.getIndex() != null) {
            Object collection = resolveCollection(propertyTokenizer, map);
            setCollectionValue(propertyTokenizer, collection, object);
        } else {
            map.put(propertyTokenizer.getName(), object);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
            if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return propertyMetaObject.getGetterType(propertyTokenizer.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
            if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return propertyMetaObject.getSetterType(propertyTokenizer.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            if (map.containsKey(propertyTokenizer.getIndexedName())) {
                MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyTokenizer.getIndexedName());
                if (propertyMetaObject == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return propertyMetaObject.hasGetter(propertyTokenizer.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return map.containsKey(propertyTokenizer.getName());
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer propertyTokenizer, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(propertyTokenizer, map);
        return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
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
}
