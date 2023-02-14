package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.factory.DefaultObjectFactory;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.wrapper.DefaultObjectWrapperFactory;
import com.pcz.mybatis.core.reflection.wrapper.ObjectWrapper;
import com.pcz.mybatis.core.reflection.wrapper.ObjectWrapperFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetaObjectTest {
    private ObjectFactory objectFactory;

    private ObjectWrapperFactory objectWrapperFactory;

    private ReflectorFactory reflectorFactory;

    @BeforeEach
    public void setUp() {
        objectFactory = new DefaultObjectFactory();
        objectWrapperFactory = new DefaultObjectWrapperFactory();
        reflectorFactory = new DefaultReflectorFactory();
    }

    @Test
    public void should_for_object() {
        MetaObject metaObject = MetaObject.forObject(new Object(),
                objectFactory, objectWrapperFactory, reflectorFactory);
        Assertions.assertThat(metaObject).isNotNull();
    }

    @Test
    public void should_return_null_meta_object_for_object_when_object_is_null() {
        MetaObject metaObject = MetaObject.forObject(null,
                objectFactory, objectWrapperFactory, reflectorFactory);
        Assertions.assertThat(metaObject).isEqualTo(SystemMetaObject.NULL_META_OBJECT);
    }

    @Test
    public void should_get_original_object() {
        Object object = new Object();
        MetaObject metaObject = MetaObject.forObject(object,
                objectFactory, objectWrapperFactory, reflectorFactory);
        Object originalObject = metaObject.getOriginalObject();
        Assertions.assertThat(originalObject).isEqualTo(object);
    }

    @Test
    public void should_get_object_wrapper() {
        Object object = new Object();
        MetaObject metaObject = MetaObject.forObject(object,
                objectFactory, objectWrapperFactory, reflectorFactory);
        ObjectWrapper objectWrapper = metaObject.getObjectWrapper();
        Assertions.assertThat(objectWrapper).isNotNull();
    }

    @Test
    public void should_get_object_factory() {
        Object object = new Object();
        MetaObject metaObject = MetaObject.forObject(object,
                objectFactory, objectWrapperFactory, reflectorFactory);
        ObjectFactory objectFactory = metaObject.getObjectFactory();
        Assertions.assertThat(objectFactory).isNotNull();
    }

    @Test
    public void should_get_object_wrapper_factory() {
        Object object = new Object();
        MetaObject metaObject = MetaObject.forObject(object,
                objectFactory, objectWrapperFactory, reflectorFactory);
        ObjectWrapperFactory objectWrapperFactory = metaObject.getObjectWrapperFactory();
        Assertions.assertThat(objectWrapperFactory).isNotNull();
    }

    @Test
    public void should_get_reflector_factory() {
        Object object = new Object();
        MetaObject metaObject = MetaObject.forObject(object,
                objectFactory, objectWrapperFactory, reflectorFactory);
        ReflectorFactory reflectorFactory = metaObject.getReflectorFactory();
        Assertions.assertThat(reflectorFactory).isNotNull();
    }

    @Test
    public void should_find_property() {
        String propertyName = "name";
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);
        String property = metaObject.findProperty(propertyName, false);
        Assertions.assertThat(propertyName).isNotNull();
    }

    @Test
    public void should_get_getter_names() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);
        String[] names = metaObject.getGetterNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_setter_names() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);
        String[] names = metaObject.getSetterNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_has_getter() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        String name = "name";
        boolean hasGetter = metaObject.hasGetter(name);
        Assertions.assertThat(hasGetter).isTrue();
    }

    @Test
    public void should_has_setter() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        String name = "name";
        boolean hasSetter = metaObject.hasSetter(name);
        Assertions.assertThat(hasSetter).isTrue();
    }

    @Test
    public void should_get_vale() {
        SubDemo subDemo = new SubDemo("subName");
        Demo demo = new Demo("name");
        demo.setSubDemo(subDemo);

        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        String propertyName = "name";
        Object value = metaObject.getValue(propertyName);
        Assertions.assertThat(value).isEqualTo("name");

        propertyName = "subDemo.name";
        value = metaObject.getValue(propertyName);
        Assertions.assertThat(value).isEqualTo("subName");
    }

    @Test
    public void should_set_vale() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        String propertyName = "name";
        metaObject.setValue(propertyName, "name");

        propertyName = "subDemo.name";
        metaObject.setValue(propertyName, "subName");
    }

    @Test
    public void should_get_meta_object_for_property() {
        Demo demo = new Demo("name");
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        String propertyName = "name";
        MetaObject propertyMetaObject = metaObject.metaObjectForProperty(propertyName);
        Assertions.assertThat(propertyMetaObject).isNotNull();
    }

    @Test
    public void should_get_getter_type() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        Class<?> type = metaObject.getGetterType("name");
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void should_get_setter_type() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        Class<?> type = metaObject.getSetterType("name");
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void should_be_collection() {
        List list = new ArrayList();
        MetaObject metaObject = MetaObject.forObject(list,
                objectFactory, objectWrapperFactory, reflectorFactory);

        boolean isCollection = metaObject.isCollection();
        Assertions.assertThat(isCollection).isTrue();
    }

    @Test
    public void should_not_be_collection() {
        Demo demo = new Demo();
        MetaObject metaObject = MetaObject.forObject(demo,
                objectFactory, objectWrapperFactory, reflectorFactory);

        boolean isCollection = metaObject.isCollection();
        Assertions.assertThat(isCollection).isFalse();
    }

    @Test
    public void should_add() {
        List<Object> list = new ArrayList();
        MetaObject metaObject = MetaObject.forObject(list,
                objectFactory, objectWrapperFactory, reflectorFactory);

        metaObject.add(new Object());
    }

    @Test
    public void should_add_all() {
        List<Object> list = new ArrayList();
        MetaObject metaObject = MetaObject.forObject(list,
                objectFactory, objectWrapperFactory, reflectorFactory);

        metaObject.addAll(Collections.singletonList(new Object()));
    }

    private static class Demo {
        private String name;

        private SubDemo subDemo;

        public Demo() {
        }

        public Demo(String name) {
            this.name = name;
        }

        public void setSubDemo(SubDemo subDemo) {
            this.subDemo = subDemo;
        }
    }

    private static class SubDemo {
        private String name;

        public SubDemo() {
        }

        public SubDemo(String name) {
            this.name = name;
        }
    }
}
