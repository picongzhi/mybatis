package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.cache.Cache;
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
     * 资源
     */
    private final String resource;

    /**
     * 当前命名空间
     */
    private String currentNamespace;

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

    public Cache useCacheRef(String namespace) {
        // TODO: 实现 useCacheRef
        return null;
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

    public Cache useNewCache(Class<? extends Cache> cacheClass,
                             Class<? extends Cache> evictionClass,
                             Long flushInterval,
                             Integer size,
                             boolean readWrite,
                             boolean blocking,
                             Properties properties) {
        // TODO: 实现 useNewCache
        return null;
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
}
