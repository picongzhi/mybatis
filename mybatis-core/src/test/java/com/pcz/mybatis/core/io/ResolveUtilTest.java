package com.pcz.mybatis.core.io;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Set;

@Deprecated
public class ResolveUtilTest {
    @Test
    public void should_find_implementations() {
        String packageName = "com.pcz.mybatis.core.io";
        Class<?> parent = Object.class;

        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.findImplementations(parent, packageName);

        Set<Class<? extends Class<?>>> classes = resolveUtil.getClasses();
        Assertions.assertThat(classes).isNotEmpty();
    }

    @Test
    public void should_return_empty_to_find_implementations_when_package_name_is_null() {
        String[] packageNames = null;
        Class<?> parent = Object.class;

        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.findImplementations(parent, packageNames);

        Set<Class<? extends Class<?>>> classes = resolveUtil.getClasses();
        Assertions.assertThat(classes).isEmpty();
    }

    @Test
    public void should_find_annotated() {
        String packageName = "com.pcz.mybatis.core.io";
        Class<? extends Annotation> annotation = Deprecated.class;

        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.findAnnotated(annotation, packageName);

        Set<Class<? extends Class<?>>> classes = resolveUtil.getClasses();
        Assertions.assertThat(classes).isNotEmpty();
    }

    @Test
    public void should_return_empty_to_find_annotated_when_package_name_is_null() {
        String[] packageNames = null;
        Class<? extends Annotation> annotation = Deprecated.class;

        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.findAnnotated(annotation, packageNames);

        Set<Class<? extends Class<?>>> classes = resolveUtil.getClasses();
        Assertions.assertThat(classes).isEmpty();
    }
}
