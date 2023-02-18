package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.impl.PerpetualCache;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class ScheduledCacheTest {
    private ScheduledCache cache;

    @BeforeEach
    public void setUp() {
        String id = new Random().toString();
        cache = new ScheduledCache(new PerpetualCache(id));
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
    public void should_get_object_when_stale() {
        long clearInterval = 1000L;
        cache.setClearInterval(clearInterval);

        String key = "key";
        Object value = new Object();
        cache.putObject(key, value);

        try {
            Thread.sleep(clearInterval + 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Object result = cache.getObject(key);
        Assertions.assertThat(result).isNull();
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
}
