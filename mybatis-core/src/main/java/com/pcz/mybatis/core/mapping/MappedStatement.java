package com.pcz.mybatis.core.mapping;

import java.util.List;

/**
 * 映射的语句
 *
 * @author picongzhi
 */
public class MappedStatement {
    /**
     * id
     */
    private String id;

    /**
     * 资源路径
     */
    private String resource;

    /**
     * 语句类型
     */
    private StatementType statementType;

    /**
     * Sql 命令类型
     */
    private SqlCommandType sqlCommandType;

    /**
     * 结果 Map
     */
    private List<ResultMap> resultMaps;

    public String getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }
}
