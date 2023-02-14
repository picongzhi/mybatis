package com.pcz.mybatis.core.mapping;

import com.pcz.mybatis.core.session.Configuration;

import java.util.List;
import java.util.Set;

/**
 * 结果 Map
 *
 * @author picongzhi
 */
public class ResultMap {
    /**
     * 配置
     */
    private Configuration configuration;

    /**
     * id
     */
    private String id;

    /**
     * 结果类型
     */
    private Class<?> type;

    /**
     * 结果映射
     */
    private List<ResultMapping> resultMappings;

    /**
     * id 结果映射
     */
    private List<ResultMapping> idResultMappings;

    /**
     * 构造器结果映射
     */
    private List<ResultMapping> constructorResultMappings;

    /**
     * 属性结果映射
     */
    private List<ResultMapping> propertyResultMappings;

    /**
     * 映射的列
     */
    private Set<String> mappedColumns;

    /**
     * 映射的属性
     */
    private Set<String> mappedProperties;

    /**
     * 判别器
     */
    private Discriminator discriminator;

    /**
     * 是否有嵌套的结果 Map
     */
    private boolean hasNestedResultMaps;

    /**
     * 是否有嵌套的查询
     */
    private boolean hasNestedQueries;

    /**
     * 是否自动映射
     */
    private Boolean autoMapping;

    private ResultMap() {
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public static class Builder {
        /**
         * ResultMap
         */
        private ResultMap resultMap = new ResultMap();

        public Builder(Configuration configuration,
                       String id,
                       Class<?> type,
                       List<ResultMapping> resultMappings) {
            this(configuration, id, type, resultMappings, null);
        }

        public Builder(Configuration configuration,
                       String id,
                       Class<?> type,
                       List<ResultMapping> resultMappings,
                       Boolean autoMapping) {
            resultMap.configuration = configuration;
            resultMap.id = id;
            resultMap.type = type;
            resultMap.resultMappings = resultMappings;
            resultMap.autoMapping = autoMapping;
        }
    }
}
