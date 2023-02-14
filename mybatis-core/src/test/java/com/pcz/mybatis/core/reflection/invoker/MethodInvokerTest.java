package com.pcz.mybatis.core.reflection.invoker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class MethodInvokerTest {
    @Test
    public void should_invoke() throws Exception {
        Method method = Demo.class.getDeclaredMethod("sayHello");
        MethodInvoker invoker = new MethodInvoker(method);

        Demo demo = new Demo();
        Object result = invoker.invoke(demo, new Object[0]);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void should_invoke_when_not_accessible() throws Exception {
        Method method = Demo.class.getDeclaredMethod("hello");
        MethodInvoker invoker = new MethodInvoker(method);

        Demo demo = new Demo();
        Object result = invoker.invoke(demo, new Object[0]);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void should_get_type() throws Exception {
        Method method = Demo.class.getDeclaredMethod("sayHello");
        MethodInvoker invoker = new MethodInvoker(method);
        Class<?> type = invoker.getType();
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    private static class Demo {
        private String hello() {
            return "hello";
        }

        public String sayHello() {
            return "hello";
        }
    }
}
