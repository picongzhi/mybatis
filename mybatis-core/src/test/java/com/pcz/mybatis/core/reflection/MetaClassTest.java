package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.invoker.Invoker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MetaClassTest {
    private MetaClass metaClass;

    private ReflectorFactory reflectorFactory;

    @BeforeEach
    public void setUp() {
        reflectorFactory = new DefaultReflectorFactory();
        metaClass = MetaClass.forClass(Demo.class, reflectorFactory);
    }

    @Test
    public void should_get_meta_class_for_class() {
        Assertions.assertThat(metaClass).isNotNull();
    }

    @Test
    public void should_get_meta_class_for_property() {
        String name = "name";
        MetaClass propertyMetaClass = metaClass.metaClassForProperty(name);
        Assertions.assertThat(propertyMetaClass).isNotNull();
    }

    @Test
    public void should_find_property() {
        String name = "name";
        String property = metaClass.findProperty(name);
        Assertions.assertThat(property).isNotNull();

        name = "subDemo.name";
        property = metaClass.findProperty(name);
        Assertions.assertThat(property).isNotNull();
    }

    @Test
    public void should_find_property_use_camel_case_mapping() {
        String name = "first_name";
        String property = metaClass.findProperty(name, true);
        Assertions.assertThat(property).isNotNull();
    }

    @Test
    public void should_not_found_property_when_not_use_camel_case_mapping() {
        String name = "first_name";
        String property = metaClass.findProperty(name, false);
        Assertions.assertThat(property).isNull();
    }

    @Test
    public void should_get_getter_names() {
        String[] names = metaClass.getGetterNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_setter_names() {
        String[] names = metaClass.getSetterNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_getter_type() {
        String name = "name";
        Class<?> type = metaClass.getGetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);

        name = "subDemo.name";
        type = metaClass.getGetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);

        name = "names[0]";
        type = metaClass.getGetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void should_get_setter_type() {
        String name = "name";
        Class<?> type = metaClass.getSetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);

        name = "subDemo.name";
        type = metaClass.getSetterType(name);
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void should_has_getter() {
        String name = "name";
        boolean hasGetter = metaClass.hasGetter(name);
        Assertions.assertThat(hasGetter).isTrue();

        name = "subDemo.name";
        hasGetter = metaClass.hasGetter(name);
        Assertions.assertThat(hasGetter).isTrue();
    }

    @Test
    public void should_not_has_getter() {
        String name = "notFound";
        boolean hasGetter = metaClass.hasGetter(name);
        Assertions.assertThat(hasGetter).isFalse();

        name = "notFound.notFound";
        hasGetter = metaClass.hasGetter(name);
        Assertions.assertThat(hasGetter).isFalse();
    }

    @Test
    public void should_has_setter() {
        String name = "name";
        boolean hasSetter = metaClass.hasSetter(name);
        Assertions.assertThat(hasSetter).isTrue();

        name = "subDemo.name";
        hasSetter = metaClass.hasSetter(name);
        Assertions.assertThat(hasSetter).isTrue();
    }

    @Test
    public void should_not_has_setter() {
        String name = "notFound";
        boolean hasSetter = metaClass.hasSetter(name);
        Assertions.assertThat(hasSetter).isFalse();

        name = "notFound.notFound";
        hasSetter = metaClass.hasSetter(name);
        Assertions.assertThat(hasSetter).isFalse();
    }

    @Test
    public void should_get_get_invoker() {
        String name = "name";
        Invoker invoker = metaClass.getGetInvoker(name);
        Assertions.assertThat(invoker).isNotNull();
    }

    @Test
    public void should_get_set_invoker() {
        String name = "name";
        Invoker invoker = metaClass.getSetInvoker(name);
        Assertions.assertThat(invoker).isNotNull();
    }

    @Test
    public void should_has_default_constructor() {
        Assertions.assertThat(metaClass.hasDefaultConstructor()).isTrue();
    }

    private static class Demo {
        private String name;

        private String firstName;

        private SubDemo subDemo;

        private List<String> names;
    }

    private static class SubDemo {
        private String name;
    }
}
