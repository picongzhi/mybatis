package com.pcz.mybatis.core.transaction.jdbc;

import com.pcz.mybatis.core.session.TransactionIsolationalLevel;
import com.pcz.mybatis.core.transaction.Transaction;
import com.pcz.mybatis.core.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * JDBC 事务工厂
 *
 * @author picongzhi
 */
public class JdbcTransactionFactory implements TransactionFactory {
    /**
     * 在事务关闭时，是否跳过自动提交
     */
    private boolean skipSetAutoCommitOnClose;

    @Override
    public void setProperties(Properties properties) {
        if (properties == null) {
            return;
        }

        String value = properties.getProperty("skipSetAutoCommitOnClose");
        if (value != null) {
            skipSetAutoCommitOnClose = Boolean.parseBoolean(value);
        }
    }

    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationalLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit, skipSetAutoCommitOnClose);
    }
}
