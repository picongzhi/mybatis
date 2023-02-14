package com.pcz.mybatis.core.io;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;

public class ClassLoaderWrapperTest {
    private ClassLoaderWrapper classLoaderWrapper;

    @BeforeEach
    public void setUp() {
        classLoaderWrapper = new ClassLoaderWrapper();
    }

    @Test
    public void should_get_resource_as_url() {
        String resource = "resources.properties";
        URL url = classLoaderWrapper.getResourceAsURL(resource);
        Assertions.assertThat(url).isNotNull();
    }

    @Test
    public void should_get_resource_as_url_with_class_loader() {
        String resource = "resources.properties";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoaderWrapper.getResourceAsURL(resource, classLoader);
        Assertions.assertThat(url).isNotNull();
    }

    @Test
    public void should_get_resource_as_stream() {
        String resource = "resources.properties";
        InputStream inputStream = classLoaderWrapper.getResourceAsStream(resource);
        Assertions.assertThat(inputStream).isNotNull();
    }

    @Test
    public void should_get_resource_as_stream_with_class_loader() {
        String resource = "resources.properties";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoaderWrapper.getResourceAsStream(resource, classLoader);
        Assertions.assertThat(inputStream).isNotNull();
    }

    @Test
    public void should_get_class_for_name() throws ClassNotFoundException {
        String name = ClassLoaderWrapperTest.class.getName();
        Class<?> cls = classLoaderWrapper.classForName(name);
        Assertions.assertThat(cls).isEqualTo(ClassLoaderWrapperTest.class);
    }

    @Test
    public void should_get_class_for_name_with_class_loader() throws ClassNotFoundException {
        String name = ClassLoaderWrapperTest.class.getName();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = classLoaderWrapper.classForName(name, classLoader);
        Assertions.assertThat(cls).isNotNull();
    }
}
