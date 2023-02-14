package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.ReflectionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObjectWrapperFactoryTest {
    private ObjectWrapperFactory objectWrapperFactory;

    @BeforeEach
    public void setUp() {
        objectWrapperFactory = new DefaultObjectWrapperFactory();
    }

    @Test
    public void should_not_has_wrapper_for() {
        boolean hasWrapperFor = objectWrapperFactory.hasWrapperFor(new Object());
        Assertions.assertThat(hasWrapperFor).isFalse();
    }

    @Test
    public void should_throw_reflection_exception_to_get_wrapper_for() {
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> objectWrapperFactory.getWrapperFor(null, new Object()));
    }
}
