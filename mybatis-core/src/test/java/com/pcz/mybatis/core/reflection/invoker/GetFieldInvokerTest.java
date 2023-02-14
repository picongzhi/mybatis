package com.pcz.mybatis.core.reflection.invoker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class GetFieldInvokerTest {
    @Test
    public void should_invoke() throws Exception {
        Field field = Demo.class.getField("publicName");
        GetFieldInvoker invoker = new GetFieldInvoker(field);

        String name = "name";
        Demo demo = new Demo();
        demo.setPublicName(name);

        Object result = invoker.invoke(demo, null);
        Assertions.assertThat(result).isEqualTo(name);
    }

    @Test
    public void should_invoke_when_not_accessible() throws Exception {
        Field field = Demo.class.getDeclaredField("privateName");
        GetFieldInvoker invoker = new GetFieldInvoker(field);

        String name = "name";
        Demo demo = new Demo();
        demo.setPrivateName(name);

        Object result = invoker.invoke(demo, null);
        Assertions.assertThat(result).isEqualTo(name);
    }

    @Test
    public void should_get_type() throws Exception {
        Field field = Demo.class.getField("publicName");
        GetFieldInvoker invoker = new GetFieldInvoker(field);

        Class<?> type = invoker.getType();
        Assertions.assertThat(type).isEqualTo(String.class);
    }

    private static class Demo {
        private String privateName;

        public String publicName;

        public void setPrivateName(String privateName) {
            this.privateName = privateName;
        }

        public void setPublicName(String publicName) {
            this.publicName = publicName;
        }
    }
}
