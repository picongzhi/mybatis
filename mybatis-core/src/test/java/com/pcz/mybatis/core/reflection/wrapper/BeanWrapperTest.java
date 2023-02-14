package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.DefaultReflectorFactory;
import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.factory.DefaultObjectFactory;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BeanWrapperTest {
    private BeanWrapper beanWrapper;

    private Demo demo;

    private MetaObject metaObject;

    @BeforeEach
    public void setUp() {
        demo = new Demo();
        metaObject = MetaObject.forObject(demo,
                new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        beanWrapper = new BeanWrapper(metaObject, demo);
    }

    @Test
    public void should_get() {
        demo.setName("name");
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer("name");
        Object result = beanWrapper.get(propertyTokenizer);
        Assertions.assertThat(result).isNotNull();
    }

    private static class Demo {
        String name;

        public void setName(String name) {
            this.name = name;
        }
    }
}
