package com.pcz.mybatis.core.io;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassLoaderWrapperTest {
    private ClassLoaderWrapper classLoaderWrapper;

    @BeforeEach
    public void setUp() {
        classLoaderWrapper = new ClassLoaderWrapper();
    }

    @Test
    public void should_get_class_for_name() throws ClassNotFoundException {
        String name = ClassLoaderWrapperTest.class.getName();
        Class<?> cls = classLoaderWrapper.classForName(name);
        Assertions.assertThat(cls).isEqualTo(ClassLoaderWrapperTest.class);
    }
}
