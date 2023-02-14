package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.cache.Cache;

/**
 * cache-ref 解析器
 *
 * @author picongzhi
 */
public class CacheRefResolver {
    /**
     * Mapper 构造助手
     */
    private final MapperBuilderAssistant assistant;

    /**
     * cache-ref 命名空间
     */
    private final String cacheRefNamespace;

    public CacheRefResolver(MapperBuilderAssistant assistant, String cacheRefNamespace) {
        this.assistant = assistant;
        this.cacheRefNamespace = cacheRefNamespace;
    }

    /**
     * 解析 CacheRef
     *
     * @return Cache
     */
    public Cache resolveCacheRef() {
        return assistant.useCacheRef(cacheRefNamespace);
    }
}
