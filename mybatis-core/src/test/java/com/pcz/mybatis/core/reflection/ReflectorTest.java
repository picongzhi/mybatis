package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.invoker.Invoker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectorTest {
    @Test
    public void should_instantiate() {
        Reflector reflector = new Reflector(Demo.class);
        Assertions.assertThat(reflector).isNotNull();
    }

    @Test
    public void should_get_type() {
        Reflector reflector = new Reflector(Demo.class);
        Class<?> type = reflector.getType();
        Assertions.assertThat(type).isEqualTo(Demo.class);
    }

    @Test
    public void should_has_setter() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "name";
        boolean hasSetter = reflector.hasSetter(name);
        Assertions.assertThat(hasSetter).isTrue();
    }

    @Test
    public void should_has_getter() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "name";
        boolean hasGetter = reflector.hasGetter(name);
        Assertions.assertThat(hasGetter).isTrue();
    }

    @Test
    public void should_get_getter_type() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "name";
        Class<?> getterType = reflector.getGetterType(name);
        Assertions.assertThat(getterType).isEqualTo(String.class);
    }

    @Test
    public void should_throw_reflection_exception_to_get_getter_type_when_property_not_found() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "notFound";
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> reflector.getGetterType(name));
    }

    @Test
    public void should_get_setter_type() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "name";
        Class<?> setterType = reflector.getSetterType(name);
        Assertions.assertThat(setterType).isEqualTo(String.class);
    }

    @Test
    public void should_throw_reflection_exception_to_get_setter_type_when_property_not_found() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "notFound";
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> reflector.getSetterType(name));
    }

    @Test
    public void should_get_readable_property_names() {
        Reflector reflector = new Reflector(Demo.class);
        String[] names = reflector.getReadablePropertyNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_writable_property_names() {
        Reflector reflector = new Reflector(Demo.class);
        String[] names = reflector.getWritablePropertyNames();
        Assertions.assertThat(names).isNotEmpty();
    }

    @Test
    public void should_get_get_invoker() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "name";
        Invoker invoker = reflector.getGetInvoker(name);
        Assertions.assertThat(invoker).isNotNull();
    }

    @Test
    public void should_throw_reflection_exception_to_get_get_invoker_when_property_not_found() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "notFound";
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> reflector.getGetInvoker(name));
    }

    @Test
    public void should_get_set_invoker() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "name";
        Invoker invoker = reflector.getSetInvoker(name);
        Assertions.assertThat(invoker).isNotNull();
    }

    @Test
    public void should_throw_reflection_exception_to_get_set_invoker_when_property_not_found() {
        Reflector reflector = new Reflector(Demo.class);
        String name = "notFound";
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> reflector.getSetInvoker(name));
    }

    @Test
    public void should_control_member_accessible() {
        Assertions.assertThat(Reflector.canControlMemberAccessible()).isTrue();
    }

    private static class Demo {
        private String name;

        private boolean valid;

        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(Integer name) {
            this.name = String.valueOf(name);
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getValid() {
            return valid;
        }

        public boolean isValid() {
            return valid;
        }

        public void setAge(Number age) {
            this.age = (Integer) age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
