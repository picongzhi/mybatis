package com.pcz.mybatis.core.reflection.invoker;

import com.pcz.mybatis.core.reflection.ReflectionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class AmbiguousMethodInvokerTest {
    @Test
    public void should_throw_reflection_exception_to_invoke() throws Exception {
        Method method = Demo.class.getDeclaredMethod("hello");
        AmbiguousMethodInvoker invoker = new AmbiguousMethodInvoker(method, "not support");

        Demo demo = new Demo();
        Assertions.assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(() -> invoker.invoke(demo, new Object[0]));
    }

    private static class Demo {
        public String hello() {
            return "hello";
        }
    }
}
