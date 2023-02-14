package com.pcz.mybatis.core.transaction;

import com.pcz.mybatis.core.session.TransactionIsolationalLevel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * 事务工厂
 *
 * @author picongzhi
 */
public interface TransactionFactory {
    /**
     * 设置属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
    }

    /**
     * 新建事务
     *
     * @param connection 连接
     * @return 事务
     */
    Transaction newTransaction(Connection connection);

    /**
     * 新建事务
     *
     * @param dataSource 数据源
     * @param level      隔离级别
     * @param autoCommit 是否自动提交
     * @return 事务
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationalLevel level, boolean autoCommit);
}
