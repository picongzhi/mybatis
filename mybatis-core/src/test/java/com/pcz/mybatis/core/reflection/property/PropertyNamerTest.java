package com.pcz.mybatis.core.reflection.property;

import com.pcz.mybatis.core.reflection.ReflectionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyNamerTest {
    @Test
    public void should_be_getter() {
        String name = "getName";
        boolean isGetter = PropertyNamer.isGetter(name);
        Assertions.assertThat(isGetter).isTrue();

        name = "isValid";
        isGetter = PropertyNamer.isGetter(name);
        Assertions.assertThat(isGetter).isTrue();
    }

    @Test
    public void should_not_be_getter() {
        String name = "setName";
        boolean isGetter = PropertyNamer.isGetter(name);
        Assertions.assertThat(isGetter).isFalse();
    }

    @Test
    public void should_be_setter() {
        String name = "setName";
        boolean isSetter = PropertyNamer.isSetter(name);
        Assertions.assertThat(isSetter).isTrue();
    }

    @Test
    public void should_not_be_setter() {
        String name = "getName";
        boolean isGetter = PropertyNamer.isSetter(name);
        Assertions.assertThat(isGetter).isFalse();
    }

    @Test
    public void should_method_to_property() {
        String name = "isValid";
        String property = PropertyNamer.methodToProperty(name);
        Assertions.assertThat(property).isEqualTo("valid");

        name = "getName";
        property = PropertyNamer.methodToProperty(name);
        Assertions.assertThat(property).isEqualTo("name");

        name = "getIP";
        property = PropertyNamer.methodToProperty(name);
        Assertions.assertThat(property).isEqualTo("IP");
    }

    @Test
    public void should_throw_reflection_exception_from_method_to_property_when_method_is_not_getter_or_setter() {
        String name = "hello";
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> PropertyNamer.methodToProperty(name));
    }
}
