package com.pcz.mybatis.core.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源工厂
 *
 * @author picongzhi
 */
public interface DataSourceFactory {
    /**
     * 设置属性
     *
     * @param properties 属性
     */
    void setProperty(Properties properties);

    /**
     * 获取数据源
     *
     * @return 数据源
     */
    DataSource getDataSource();
}
