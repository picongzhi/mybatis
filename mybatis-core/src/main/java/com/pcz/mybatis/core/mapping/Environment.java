package com.pcz.mybatis.core.mapping;

import com.pcz.mybatis.core.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * 环境
 *
 * @author picongzhi
 */
public class Environment {
    /**
     * 环境id
     */
    private final String id;

    /**
     * 事务工厂
     */
    private final TransactionFactory transactionFactory;

    /**
     * 数据源
     */
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        if (id == null) {
            throw new IllegalArgumentException("Parameter 'id' must not be null");
        }

        if (transactionFactory == null) {
            throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
        }

        if (dataSource == null) {
            throw new IllegalArgumentException("Parameter 'datasource' must not be null");
        }

        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 环境构造器
     */
    public static class Builder {
        /**
         * 环境id
         */
        private final String id;

        /**
         * 事务工厂
         */
        private TransactionFactory transactionFactory;

        /**
         * 数据源
         */
        private DataSource dataSource;

        public Builder(String id) {
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder datasource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public String id() {
            return id;
        }

        public Environment build() {
            return new Environment(id, transactionFactory, dataSource);
        }
    }
}
