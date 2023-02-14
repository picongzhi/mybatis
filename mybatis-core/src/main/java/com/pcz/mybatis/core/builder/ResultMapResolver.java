package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.mapping.Discriminator;
import com.pcz.mybatis.core.mapping.ResultMap;
import com.pcz.mybatis.core.mapping.ResultMapping;

import java.util.List;

/**
 * ResultMap 解析器
 *
 * @author picongzhi
 */
public class ResultMapResolver {
    /**
     * Mapper 构造助手
     */
    private final MapperBuilderAssistant assistant;

    /**
     * id
     */
    private final String id;

    /**
     * 类型
     */
    private final Class<?> type;

    /**
     * 继承自
     */
    private String extend;

    /**
     * 判别器
     */
    private final Discriminator discriminator;

    /**
     * 结果映射
     */
    private final List<ResultMapping> resultMappings;

    /**
     * 是否自动映射
     */
    private final Boolean autoMapping;

    public ResultMapResolver(MapperBuilderAssistant assistant,
                             String id,
                             Class<?> type,
                             String extend,
                             Discriminator discriminator,
                             List<ResultMapping> resultMappings,
                             Boolean autoMapping) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.extend = extend;
        this.discriminator = discriminator;
        this.resultMappings = resultMappings;
        this.autoMapping = autoMapping;
    }

    public ResultMap resolve() {
        return assistant.addResultMap(
                this.id, this.type, this.extend, this.discriminator, this.resultMappings, this.autoMapping);
    }
}
