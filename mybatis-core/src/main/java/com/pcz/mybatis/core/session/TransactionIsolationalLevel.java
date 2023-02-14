package com.pcz.mybatis.core.session;

import java.sql.Connection;

/**
 * 事务隔离级别
 *
 * @author picongzhi
 */
public enum TransactionIsolationalLevel {
    /**
     * 无
     */
    NONE(Connection.TRANSACTION_NONE),
    /**
     * 读已提交
     */
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    /**
     * 读未提交
     */
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    /**
     * 可重复度
     */
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    /**
     * 串行化
     */
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    /**
     * 隔离级别
     */
    private final int level;

    TransactionIsolationalLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
