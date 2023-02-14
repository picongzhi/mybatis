package com.pcz.mybatis.core.reflection.invoker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class SetFieldInvokerTest {
    @Test
    public void should_invoke() throws Exception {
        Field field = Demo.class.getField("publicName");
        SetFieldInvoker invoker = new SetFieldInvoker(field);

        Demo demo = new Demo();
        String name = "name";
        invoker.invoke(demo, new Object[]{name});
    }

    @Test
    public void should_invoke_when_not_accessible() throws Exception {
        Field field = Demo.class.getDeclaredField("privateName");
        SetFieldInvoker invoker = new SetFieldInvoker(field);

        Demo demo = new Demo();
        String name = "name";
        invoker.invoke(demo, new Object[]{name});
    }

    @Test
    public void should_get_type() throws Exception {
        Field field = Demo.class.getField("publicName");
        SetFieldInvoker invoker = new SetFieldInvoker(field);

        Class<?> type = invoker.getType();
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    private static class Demo {
        private String privateName;

        public String publicName;
    }
}
