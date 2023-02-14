package com.pcz.mybatis.core.util;

import java.util.Map;
import java.util.function.Function;

/**
 * Map 工具类
 *
 * @author picongzhi
 */
public class MapUtil {
    /**
     * 根据 key 获取 value，如果不存在，使用映射函数计算
     *
     * @param map             map
     * @param key             key
     * @param mappingFunction 映射函数
     * @param <K>             key 泛型
     * @param <V>             value 泛型
     * @return 值
     */
    public static <K, V> V compoteIfAbsent(Map<K, V> map,
                                           K key,
                                           Function<K, V> mappingFunction) {
        V value = map.get(key);
        return value != null ?
                value : map.computeIfAbsent(key, mappingFunction);
    }
}
