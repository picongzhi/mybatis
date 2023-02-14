package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.DefaultReflectorFactory;
import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.ReflectorFactory;
import com.pcz.mybatis.core.reflection.factory.DefaultObjectFactory;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapWrapperTest {
    private ObjectFactory objectFactory;

    private ObjectWrapperFactory objectWrapperFactory;

    private ReflectorFactory reflectorFactory;

    private Map<String, Object> map;

    private MapWrapper mapWrapper;

    @BeforeEach
    public void setUp() {
        objectFactory = new DefaultObjectFactory();
        objectWrapperFactory = new DefaultObjectWrapperFactory();
        reflectorFactory = new DefaultReflectorFactory();

        map = new HashMap<>();
        MetaObject metaObject = MetaObject.forObject(map, objectFactory, objectWrapperFactory, reflectorFactory);
        mapWrapper = new MapWrapper(metaObject, map);
    }

    @Test
    public void should_get() {
        String key = "key";
        Object value = new Object();
        map.put(key, value);

        Object result = mapWrapper.get(new PropertyTokenizer(key));
        Assertions.assertThat(result).isEqualTo(value);

        key = "[key]";
        result = mapWrapper.get(new PropertyTokenizer(key));
        Assertions.assertThat(result).isEqualTo(value);
    }

    @Test
    public void should_set() {
        String key = "key";
        Object value = new Object();
        mapWrapper.set(new PropertyTokenizer(key), value);

        key = "[key]";
        mapWrapper.set(new PropertyTokenizer(key), value);
    }

    @Test
    public void should_find_property() {
        String key = "key";
        String property = mapWrapper.findProperty(key, false);
        Assertions.assertThat(property).isEqualTo(key);
    }

    @Test
    public void should_get_getter_names() {
        String key = "key";
        Object value = new Object();
        map.put(key, value);

        String[] names = mapWrapper.getGetterNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_setter_names() {
        String key = "key";
        Object value = new Object();
        map.put(key, value);

        String[] names = mapWrapper.getSetterNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_getter_type() {
        String key = "key";
        Object value = new Object();
        map.put(key, value);

        String name = "key";
        Class<?> type = mapWrapper.getGetterType(name);
        Assertions.assertThat(type).isEqualTo(Object.class);

        map.put(key, new Demo());

        name = "key.name";
        type = mapWrapper.getGetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void should_return_object_type_to_get_getter_type_when_key_not_found() {
        String name = "key";
        Class<?> type = mapWrapper.getGetterType(name);
        Assertions.assertThat(type).isEqualTo(Object.class);
    }

    @Test
    public void should_get_setter_type() {
        String key = "key";
        Object value = new Object();
        map.put(key, value);

        String name = "key";
        Class<?> type = mapWrapper.getSetterType(name);
        Assertions.assertThat(type).isEqualTo(Object.class);

        map.put(key, new Demo());

        name = "key.name";
        type = mapWrapper.getSetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void should_return_object_type_to_get_setter_type_when_key_not_found() {
        String name = "key";
        Class<?> type = mapWrapper.getSetterType(name);
        Assertions.assertThat(type).isEqualTo(Object.class);
    }

    @Test
    public void should_has_getter() {
        String key = "key";
        Object value = new Object();
        map.put(key, value);

        String name = "key";
        boolean hasGetter = mapWrapper.hasGetter(name);
        Assertions.assertThat(hasGetter).isTrue();

        map.put(key, new Demo());

        name = "key.name";
        hasGetter = mapWrapper.hasGetter(name);
        Assertions.assertThat(hasGetter).isTrue();
    }

    @Test
    public void should_not_has_getter_type_when_key_not_found() {
        String name = "key";
        boolean hasGetter = mapWrapper.hasGetter(name);
        Assertions.assertThat(hasGetter).isFalse();
    }

    @Test
    public void should_has_setter() {
        String name = "key";
        boolean hasSetter = mapWrapper.hasSetter(name);
        Assertions.assertThat(hasSetter).isTrue();
    }

    @Test
    public void should_instantiate_property_value() {
        String name = "key";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        MetaObject metaObject = mapWrapper.instantiatePropertyValue(name, propertyTokenizer, new DefaultObjectFactory());
        Assertions.assertThat(metaObject).isNotNull();
    }

    @Test
    public void should_not_be_collection() {
        boolean isCollection = mapWrapper.isCollection();
        Assertions.assertThat(isCollection).isFalse();
    }

    @Test
    public void should_throw_unsupported_operation_exception_to_add() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> mapWrapper.add(new Object()));
    }

    @Test
    public void should_throw_unsupported_operation_exception_to_add_all() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> mapWrapper.addAll(Collections.singletonList(new Object())));
    }

    private static class Demo {
        String name;
    }
}
