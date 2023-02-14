package com.pcz.mybatis.core.transaction.jdbc;

import com.pcz.mybatis.core.session.TransactionIsolationalLevel;
import com.pcz.mybatis.core.transaction.Transaction;
import com.pcz.mybatis.core.transaction.TransactionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC 事务
 *
 * @author picongzhi
 */
public class JdbcTransaction implements Transaction {
    // TODO: 添加日志

    /**
     * 连接
     */
    protected Connection connection;

    /**
     * 数据源
     */
    protected DataSource dataSource;

    /**
     * 隔离级别
     */
    protected TransactionIsolationalLevel level;

    /**
     * 自动提交
     */
    protected boolean autoCommit;

    /**
     * 在关闭时跳过设置自动提交
     */
    protected boolean skipSetAutoCommitOnClose;

    public JdbcTransaction(DataSource dataSource,
                           TransactionIsolationalLevel desiredLevel,
                           boolean desiredAutoCommit) {
        this(dataSource, desiredLevel, desiredAutoCommit, false);
    }

    public JdbcTransaction(DataSource dataSource,
                           TransactionIsolationalLevel desiredLevel,
                           boolean desiredAutoCommit,
                           boolean skipSetAutoCommitOnClose) {
        this.dataSource = dataSource;
        this.level = desiredLevel;
        this.autoCommit = desiredAutoCommit;
        this.skipSetAutoCommitOnClose = skipSetAutoCommitOnClose;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }

        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            // 连接不为 null 且 不是自动提交
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            // 连接不为 null 且 不是自动提交
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            // 重置自动提交
            resetAutoCommit();
            // 关闭连接
            connection.close();
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }

    /**
     * 开启连接
     *
     * @throws SQLException SQL 异常
     */
    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        if (level != null) {
            // 设置隔离级别
            connection.setTransactionIsolation(level.getLevel());
        }

        // 设置是否自动提交
        setDesiredAutoCommit(autoCommit);
    }

    /**
     * 设置是否自动提交事务
     *
     * @param desiredAutoCommit 是否自动提交事务
     */
    protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
        try {
            if (connection.getAutoCommit() != desiredAutoCommit) {
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch (SQLException e) {
            throw new TransactionException("Error configuring AutoCommit. "
                    + "Your driver may not support getAutoCommit() or setAutoCommit()."
                    + "Required setting: " + desiredAutoCommit + ". Cause: " + e, e);
        }
    }

    /**
     * 重置自动提交，置为自动提交
     */
    protected void resetAutoCommit() {
        try {
            if (!skipSetAutoCommitOnClose && !connection.getAutoCommit()) {
                // 在连接关闭时不跳过设置自动提交 且 连接不是自动提交
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            // TODO: log
        }
    }
}
