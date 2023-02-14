package com.pcz.mybatis.core.mapping;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库运营商 ID Provider
 *
 * @author picongzhi
 */
public interface DatabaseIdProvider {
    /**
     * 设置属性
     *
     * @param properties 属性
     */
    default void setProperties(Properties properties) {
    }

    /**
     * 获取数据库运营商 id
     *
     * @param dataSource 数据源
     * @return id
     * @throws SQLException SQL 异常
     */
    String getDatabaseId(DataSource dataSource) throws SQLException;
}
