package com.pcz.mybatis.core.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务
 *
 * @author picongzhi
 */
public interface Transaction {
    /**
     * 获取连接
     *
     * @return 连接
     * @throws SQLException SQL 异常
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事务
     *
     * @throws SQLException SQL 异常
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     *
     * @throws SQLException SQL 异常
     */
    void rollback() throws SQLException;

    /**
     * 关闭事务
     *
     * @throws SQLException SQL 异常
     */
    void close() throws SQLException;

    /**
     * 获取超时时间
     *
     * @return 超时时间
     * @throws SQLException SQL 异常
     */
    Integer getTimeout() throws SQLException;
}
