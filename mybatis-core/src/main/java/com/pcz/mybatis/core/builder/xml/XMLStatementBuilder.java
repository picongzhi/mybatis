package com.pcz.mybatis.core.builder.xml;

import com.pcz.mybatis.core.builder.BaseBuilder;
import com.pcz.mybatis.core.builder.MapperBuilderAssistant;
import com.pcz.mybatis.core.parsing.XNode;
import com.pcz.mybatis.core.session.Configuration;

/**
 * 基于 XML 的语句构造器
 *
 * @author picongzhi
 */
public class XMLStatementBuilder extends BaseBuilder {
    /**
     * Mapper 构造器助手
     */
    private final MapperBuilderAssistant mapperBuilderAssistant;

    /**
     * 节点
     */
    private final XNode xnode;

    /**
     * 数据库 id
     */
    private final String requiredDatabaseId;

    public XMLStatementBuilder(Configuration configuration,
                               MapperBuilderAssistant mapperBuilderAssistant,
                               XNode xnode,
                               String databaseId) {
        super(configuration);
        this.mapperBuilderAssistant = mapperBuilderAssistant;
        this.xnode = xnode;
        this.requiredDatabaseId = databaseId;
    }

    public void parseStatementNode() {
        // TODO: 实现解析
    }
}
