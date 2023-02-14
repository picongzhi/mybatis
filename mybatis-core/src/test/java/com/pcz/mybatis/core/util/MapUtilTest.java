package com.pcz.mybatis.core.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MapUtilTest {
    @Test
    public void should_compute_if_absent() {
        Map<String, Object> map = new HashMap<>();
        String key = "key";
        Object result = MapUtil.compoteIfAbsent(map, key, (k) -> new Object());
        Assertions.assertThat(result).isNotNull();
    }
}
