package com.pcz.mybatis.core.mapping;

/**
 * 语句类型
 *
 * @author picongzhi
 */
public enum StatementType {
    /**
     * 普通语句
     */
    STATEMENT,
    /**
     * 预编译语句
     */
    PREPARED,
    /**
     * 存储过程
     */
    CALLABLE;
}
