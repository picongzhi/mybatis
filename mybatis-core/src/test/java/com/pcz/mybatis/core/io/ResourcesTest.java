package com.pcz.mybatis.core.io;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

public class ResourcesTest {
    @AfterEach
    public void tearDown() {
        Resources.setCharset(null);
    }

    @Test
    public void should_set_default_class_loader() {
        Resources.setDefaultClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void should_get_default_class_loader() {
        Resources.setDefaultClassLoader(Thread.currentThread().getContextClassLoader());

        ClassLoader classLoader = Resources.getDefaultClassLoader();
        Assertions.assertThat(classLoader).isNotNull();
    }

    @Test
    public void should_get_resource_url() throws IOException {
        String resource = "resources.properties";
        URL url = Resources.getResourceURL(resource);
        Assertions.assertThat(url).isNotNull();
    }

    @Test
    public void should_throw_io_exception_to_get_resource_url_when_resource_not_found() {
        String resource = "notFound";
        Assertions.assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> Resources.getResourceURL(resource));
    }

    @Test
    public void should_get_resource_as_stream() throws IOException {
        String resource = "resources.properties";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        Assertions.assertThat(inputStream).isNotNull();
    }

    @Test
    public void should_throw_io_exception_to_get_resource_as_stream_when_resource_not_found() {
        String resource = "notFound";
        Assertions.assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> Resources.getResourceAsStream(resource));
    }

    @Test
    public void should_get_resource_as_properties() throws IOException {
        String resource = "resources.properties";
        Properties properties = Resources.getResourceAsProperties(resource);
        Assertions.assertThat(properties).isNotNull();
    }

    @Test
    public void should_throw_io_exception_to_get_resource_as_properties_when_resource_not_found() {
        String resource = "notFound";
        Assertions.assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> Resources.getResourceAsProperties(resource));
    }

    @Test
    public void should_get_resource_as_properties_with_class_loader() throws IOException {
        String resource = "resources.properties";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = Resources.getResourceAsProperties(classLoader, resource);
        Assertions.assertThat(properties).isNotNull();
    }

    @Test
    public void should_throw_io_exception_to_get_resource_as_properties_with_class_loader_when_resource_not_found() {
        String resource = "notFound";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Assertions.assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> Resources.getResourceAsProperties(classLoader, resource));
    }

    @Test
    public void should_get_resource_as_reader() throws IOException {
        String resource = "resources.properties";
        Reader reader = Resources.getResourceAsReader(resource);
        Assertions.assertThat(reader).isNotNull();
    }

    @Test
    public void should_get_resource_as_reader_with_charset() throws IOException {
        Resources.setCharset(Charset.defaultCharset());

        String resource = "resources.properties";
        Reader reader = Resources.getResourceAsReader(resource);
        Assertions.assertThat(reader).isNotNull();
    }

    @Test
    public void should_get_class_for_name() throws ClassNotFoundException {
        String name = ResourcesTest.class.getName();
        Class<?> cls = Resources.classForName(name);
        Assertions.assertThat(cls).isEqualTo(ResourcesTest.class);
    }

    @Test
    public void should_get_url_as_stream() throws IOException {
        String url = "https://www.baidu.com";
        InputStream inputStream = Resources.getUrlAsStream(url);
        Assertions.assertThat(inputStream).isNotNull();
    }

    @Test
    public void should_get_url_as_properties() throws IOException {
        String url = "https://www.baidu.com";
        Properties properties = Resources.getUrlAsProperties(url);
        Assertions.assertThat(properties).isNotNull();
    }
}
