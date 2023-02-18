package com.pcz.mybatis.core.cache.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class PerpetualCacheTest {
    private PerpetualCache cache;

    @BeforeEach
    public void setUp() {
        cache = new PerpetualCache(new Random().toString());
    }

    @Test
    public void should_get_id() {
        String id = cache.getId();
        Assertions.assertThat(id).isNotNull();
    }

    @Test
    public void should_put_object() {
        String key = "key";
        Object value = new Object();
        cache.putObject(key, value);
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
    public void should_remove_object() {
        String key = "key";
        Object value = new Object();
        cache.putObject(key, value);

        Object result = cache.removeObject(key);
        Assertions.assertThat(result).isEqualTo(value);
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

    @Test
    public void should_equal() {
        String id = new Random().toString();
        Assertions.assertThat(new PerpetualCache(id))
                .isEqualTo(new PerpetualCache(id));
    }
}
