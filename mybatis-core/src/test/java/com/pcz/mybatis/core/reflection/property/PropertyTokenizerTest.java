package com.pcz.mybatis.core.reflection.property;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyTokenizerTest {
    @Test
    public void should_get_name() {
        String propertyName = "a";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        String name = propertyTokenizer.getName();
        Assertions.assertThat(name).isEqualTo("a");

        propertyName = "a.b";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        name = propertyTokenizer.getName();
        Assertions.assertThat(name).isEqualTo("a");

        propertyName = "a[1]";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        name = propertyTokenizer.getName();
        Assertions.assertThat(name).isEqualTo("a");
    }

    @Test
    public void should_get_index() {
        String propertyName = "a";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        String index = propertyTokenizer.getIndex();
        Assertions.assertThat(index).isNull();

        propertyName = "a.b";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        index = propertyTokenizer.getIndex();
        Assertions.assertThat(index).isNull();

        propertyName = "a[1]";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        index = propertyTokenizer.getIndex();
        Assertions.assertThat(index).isEqualTo("1");
    }

    @Test
    public void should_get_indexed_name() {
        String propertyName = "a";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        String indexedName = propertyTokenizer.getIndexedName();
        Assertions.assertThat(indexedName).isEqualTo("a");

        propertyName = "a.b";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        indexedName = propertyTokenizer.getIndexedName();
        Assertions.assertThat(indexedName).isEqualTo("a");

        propertyName = "a[1]";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        indexedName = propertyTokenizer.getIndexedName();
        Assertions.assertThat(indexedName).isEqualTo("a[1]");
    }

    @Test
    public void should_get_children() {
        String propertyName = "a";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        String children = propertyTokenizer.getChildren();
        Assertions.assertThat(children).isNull();

        propertyName = "a.b";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        children = propertyTokenizer.getChildren();
        Assertions.assertThat(children).isEqualTo("b");

        propertyName = "a[1]";
        propertyTokenizer = new PropertyTokenizer(propertyName);
        children = propertyTokenizer.getChildren();
        Assertions.assertThat(children).isNull();
    }

    @Test
    public void should_has_next() {
        String propertyName = "a.b";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        boolean hasNext = propertyTokenizer.hasNext();
        Assertions.assertThat(hasNext).isTrue();
    }

    @Test
    public void should_not_has_next() {
        String propertyName = "a";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        boolean hasNext = propertyTokenizer.hasNext();
        Assertions.assertThat(hasNext).isFalse();
    }

    @Test
    public void should_get_next() {
        String propertyName = "a.b";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        PropertyTokenizer next = propertyTokenizer.next();
        Assertions.assertThat(next).isNotNull();
    }

    @Test
    public void should_throw_unsupported_operation_exception() {
        String propertyName = "a";
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(propertyName);
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> propertyTokenizer.remove());
    }
}
