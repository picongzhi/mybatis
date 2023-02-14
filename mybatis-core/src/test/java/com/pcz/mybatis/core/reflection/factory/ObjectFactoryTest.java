package com.pcz.mybatis.core.reflection.factory;

import com.pcz.mybatis.core.reflection.ReflectionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ObjectFactoryTest {
    private ObjectFactory objectFactory;

    @BeforeEach
    public void setUp() {
        objectFactory = new DefaultObjectFactory();
    }

    @Test
    public void should_create() {
        Demo demo = objectFactory.create(Demo.class);
        Assertions.assertThat(demo).isNotNull();
    }

    @Test
    public void should_create_when_constructor_not_accessible() {
        PrivateDemo privateDemo = objectFactory.create(PrivateDemo.class);
        Assertions.assertThat(privateDemo).isNotNull();
    }

    @Test
    public void should_create_with_constructor() {
        Demo demo = objectFactory.create(Demo.class,
                Arrays.asList(String.class),
                Arrays.asList("name"));
        Assertions.assertThat(demo).isNotNull();
    }

    @Test
    public void should_create_with_constructor_when_constructor_not_accessible() {
        PrivateDemo privateDemo = objectFactory.create(PrivateDemo.class,
                Arrays.asList(String.class),
                Arrays.asList("name"));
        Assertions.assertThat(privateDemo).isNotNull();
    }

    @Test
    public void should_create_when_class_is_collection() {
        List list = objectFactory.create(List.class);
        Assertions.assertThat(list).isNotNull();
    }

    @Test
    public void should_create_when_class_is_map() {
        Map map = objectFactory.create(Map.class);
        Assertions.assertThat(map).isNotNull();
    }

    @Test
    public void should_create_when_class_is_sorted_set() {
        SortedSet sortedSet = objectFactory.create(SortedSet.class);
        Assertions.assertThat(sortedSet).isNotNull();
    }

    @Test
    public void should_create_when_class_is_set() {
        Set set = objectFactory.create(Set.class);
        Assertions.assertThat(set).isNotNull();
    }

    @Test
    public void should_throw_reflection_exception_to_create_with_constructor_when_args_not_match() {
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> objectFactory.create(Demo.class,
                        Arrays.asList(String.class),
                        Arrays.asList(1)));
    }

    @Test
    public void should_be_collection() {
        boolean isCollection = objectFactory.isCollection(List.class);
        Assertions.assertThat(isCollection).isTrue();
    }

    private static class Demo {
        private String name;

        public Demo() {
        }

        public Demo(String name) {
            this.name = name;
        }
    }

    private static class PrivateDemo {
        private String name;

        private PrivateDemo() {
        }

        private PrivateDemo(String name) {
            this.name = name;
        }
    }
}
