package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.cache.decorators.LruCache;
import com.pcz.mybatis.core.cache.impl.PerpetualCache;
import com.pcz.mybatis.core.executor.ErrorContext;
import com.pcz.mybatis.core.mapping.*;
import com.pcz.mybatis.core.session.Configuration;
import com.pcz.mybatis.core.type.JdbcType;
import com.pcz.mybatis.core.type.TypeHandler;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Mapper 构造助手
 *
 * @author picongzhi
 */
public class MapperBuilderAssistant extends BaseBuilder {
    /**
     * 当前命名空间
     */
    private String currentNamespace;

    /**
     * 资源
     */
    private final String resource;

    /**
     * 当前缓存
     */
    private Cache currentCache;

    /**
     * 引用缓存是否未解决
     */
    private boolean unresolvedCacheRef;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        ErrorContext.instance().resource(resource);
        this.resource = resource;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        this.currentNamespace = currentNamespace;
    }

    /**
     * 使用引用缓存
     *
     * @param cacheRefNamespace 引用缓存的命名空间
     * @return 缓存
     */
    public Cache useCacheRef(String cacheRefNamespace) {
        if (cacheRefNamespace == null) {
            throw new BuilderException("cache-ref element requires a namespace attribute.");
        }

        try {
            unresolvedCacheRef = true;
            Cache cache = configuration.getCache(cacheRefNamespace);
            if (cache == null) {
                throw new IncompleteElementException(
                        "No cache for namespace '" + cacheRefNamespace + "' could be found.");
            }

            currentCache = cache;
            unresolvedCacheRef = false;

            return cache;
        } catch (IllegalArgumentException e) {
            throw new IncompleteElementException(
                    "No cache for namespace '" + cacheRefNamespace + "' could be found.", e);
        }
    }

    public ResultMap addResultMap(String id,
                                  Class<?> type,
                                  String extend,
                                  Discriminator discriminator,
                                  List<ResultMapping> resultMappings,
                                  Boolean autoMapping) {
        // TODO: 实现 addResultMap
        return null;
    }

    /**
     * 使用新的缓存
     *
     * @param cacheClass    缓存 Class 实例
     * @param evictionClass 缓存淘汰策略 Class 实例
     * @param flushInterval 刷新时间间隔
     * @param size          缓存竖向
     * @param readWrite     读写
     * @param blocking      是否阻塞
     * @param properties    属性
     * @return 缓存
     */
    public Cache useNewCache(Class<? extends Cache> cacheClass,
                             Class<? extends Cache> evictionClass,
                             Long flushInterval,
                             Integer size,
                             boolean readWrite,
                             boolean blocking,
                             Properties properties) {
        Cache cache = new CacheBuilder(currentNamespace)
                .implementation(valueOrDefault(cacheClass, PerpetualCache.class))
                .addDecorator(valueOrDefault(evictionClass, LruCache.class))
                .clearInterval(flushInterval)
                .size(size)
                .readWrite(readWrite)
                .blocking(blocking)
                .properties(properties)
                .build();

        configuration.addCache(cache);
        currentCache = cache;

        return cache;
    }

    public ParameterMapping buildParameterMapping(Class<?> parameterType,
                                                  String property,
                                                  Class<?> javaType,
                                                  JdbcType jdbcType,
                                                  String resultMap,
                                                  ParameterMode parameterMode,
                                                  Class<? extends TypeHandler<?>> typeHandlerClass,
                                                  Integer numericScale) {
        // TODO: 实现 buildParameterMapping
        return null;
    }

    public ParameterMap addParameterMap(String id,
                                        Class<?> parameterClass,
                                        List<ParameterMapping> parameterMappings) {

        return null;
    }

    public ResultMapping buildResultMapping(Class<?> resultType,
                                            String property,
                                            String column,
                                            Class<?> javaType,
                                            JdbcType jdbcType,
                                            String nestedSelect,
                                            String nestedResultMap,
                                            String notNullColumn,
                                            String columnPrefix,
                                            Class<? extends TypeHandler<?>> typeHandler,
                                            List<ResultFlag> flags,
                                            String resultSet,
                                            String foreignColumn,
                                            boolean lazy) {
        return null;
    }

    public Discriminator buildDiscriminator(Class<?> resultType,
                                            String column,
                                            Class<?> javaType,
                                            JdbcType jdbcType,
                                            Class<? extends TypeHandler<?>> typeHandler,
                                            Map<String, String> discriminatorMap) {
        return null;
    }

    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }

        if (isReference) {
            if (base.contains(".")) {
                return base;
            }
        } else {
            if (base.startsWith(currentNamespace + ".")) {
                return base;
            }

            if (base.contains(".")) {
                throw new BuilderException("Dots are not allowed in element names, please remove if from " + base);
            }
        }

        return currentNamespace + "." + base;
    }

    /**
     * 获取值，如果值为 null，返回默认值
     *
     * @param value        值
     * @param defaultValue 默认值
     * @param <T>          泛型
     * @return 结果
     */
    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null
                ? defaultValue
                : value;
    }
}
