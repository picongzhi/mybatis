package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.impl.PerpetualCache;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class BlockingCacheDemo {
    private BlockingCache cache;

    @BeforeEach
    public void setUp() {
        String id = new Random().toString();
        cache = new BlockingCache(new PerpetualCache(id));
    }

    @Test
    public void should_get_id() {
        String id = cache.getId();
        Assertions.assertThat(id).isNotNull();
    }

    @Test
    public void should_get_object_after_set_object() {
        String key = new Random().toString();

        Object result = cache.getObject(key);
        Assertions.assertThat(result).isNull();

        Object value = new Object();
        cache.putObject(key, value);

        result = cache.getObject(key);
        Assertions.assertThat(result).isEqualTo(value);
    }
}
