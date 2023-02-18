package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.impl.PerpetualCache;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class LruCacheTest {
    private LruCache cache;

    @BeforeEach
    public void setUp() {
        String id = new Random().toString();
        cache = new LruCache(new PerpetualCache(id));
    }

    @Test
    public void should_get_id() {
        String id = cache.getId();
        Assertions.assertThat(id).isNotNull();
    }

    @Test
    public void should_put_object() {
        cache.putObject("key", new Object());
    }

    @Test
    public void should_get_object() {
        String key = "key";
        Object value = new Object();
        cache.putObject(key, value);

        Object result = cache.getObject(key);
        Assertions.assertThat(result).isEqualTo(value);
    }

    @Test
    public void should_trigee_key_eviction_to_put_object_when_reaching_size_limit() {
        cache.setSize(3);

        cache.putObject("a", "a");
        cache.putObject("b", "b");
        cache.putObject("c", "c");
        cache.putObject("d", "d");

        Object result = cache.getObject("a");
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void should_remove_object() {
        String key = "key";
        Object value = new Object();
        cache.putObject(key, value);

        Object result = cache.removeObject(key);
        Assertions.assertThat(result).isEqualTo(result);
    }

    @Test
    public void should_clear() {
        cache.clear();
    }

    @Test
    public void should_get_size() {
        int size = cache.getSize();
        Assertions.assertThat(size).isZero();
    }
}
