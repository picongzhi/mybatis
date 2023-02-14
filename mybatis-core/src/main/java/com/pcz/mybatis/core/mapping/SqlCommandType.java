package com.pcz.mybatis.core.mapping;

/**
 * Sql 命令类型
 *
 * @author picongzhi
 */
public enum SqlCommandType {
    /**
     * 未知
     */
    UNKOWN,
    /**
     * 增
     */
    INSERT,
    /**
     * 改
     */
    UPDATE,
    /**
     * 删
     */
    DELETE,
    /**
     * 查
     */
    SELECT,
    /**
     * 刷新
     */
    FLUSH;
}
