package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

public class CollectionWrapperTest {
    private CollectionWrapper collectionWrapper;

    @BeforeEach
    public void setUp() {
        collectionWrapper = new CollectionWrapper(null, new ArrayList<>());
    }

    @Test
    public void should_throw_unsupported_exception_to_get() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.get(new PropertyTokenizer("name")));
    }

    @Test
    public void should_throw_unsupported_exception_to_set() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.set(new PropertyTokenizer("name"), new Object()));
    }

    @Test
    public void should_throw_unsupported_exception_to_find_property() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.findProperty("name", false));
    }

    @Test
    public void should_throw_unsupported_exception_to_set_getter_names() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.getGetterNames());
    }

    @Test
    public void should_throw_unsupported_exception_to_set_setter_names() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.getSetterNames());
    }

    @Test
    public void should_throw_unsupported_exception_to_get_getter_type() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.getGetterType("name"));
    }

    @Test
    public void should_throw_unsupported_exception_to_get_setter_type() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.getSetterType("name"));
    }

    @Test
    public void should_throw_unsupported_exception_to_has_getter() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.hasGetter("name"));
    }

    @Test
    public void should_throw_unsupported_exception_to_has_setter() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.hasSetter("name"));
    }

    @Test
    public void should_throw_unsupported_exception_to_instantiate_property_value() {
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> collectionWrapper.instantiatePropertyValue("name", null, null));
    }

    @Test
    public void should_be_collection() {
        boolean isCollection = collectionWrapper.isCollection();
        Assertions.assertThat(isCollection).isTrue();
    }

    @Test
    public void should_add() {
        collectionWrapper.add(new Object());
    }

    @Test
    public void should_add_all() {
        collectionWrapper.addAll(Collections.singletonList(new Object()));
    }
}
