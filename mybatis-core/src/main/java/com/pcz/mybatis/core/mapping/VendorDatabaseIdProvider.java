package com.pcz.mybatis.core.mapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * 供应商数据库 id provider
 *
 * @author picongzhi
 */
public class VendorDatabaseIdProvider implements DatabaseIdProvider {
    /**
     * 属性
     */
    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        if (dataSource == null) {
            throw new NullPointerException("dataSource cannot be null");
        }

        try {
            return getDatabaseName(dataSource);
        } catch (Exception e) {
            // TODO: 打印日志
        }

        return null;
    }

    /**
     * 获取数据库名称
     *
     * @param dataSource 数据源
     * @return 数据库名称
     * @throws SQLException SQL 异常
     */
    private String getDatabaseName(DataSource dataSource) throws SQLException {
        String productName = getDatabaseProductName(dataSource);
        if (this.properties == null) {
            return productName;
        }

        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            if (productName.contains((String) property.getKey())) {
                return (String) property.getValue();
            }
        }

        return null;
    }

    /**
     * 获取数据库产品名
     *
     * @param dataSource 数据源
     * @return 数据库产品名
     * @throws SQLException SQL 异常
     */
    private String getDatabaseProductName(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName();
        }
    }
}
