package com.pcz.mybatis.core.datasource.unpooled;

import com.pcz.mybatis.core.datasource.DataSourceException;
import com.pcz.mybatis.core.datasource.DataSourceFactory;
import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 非池化的数据源工厂
 *
 * @author picongzhi
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {
    /**
     * 数据库驱动前缀
     */
    private static final String DRIVER_PROPERTY_PREFIX = "driver.";

    /**
     * 数据库驱动前缀长度
     */
    private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();

    /**
     * 数据源
     */
    protected DataSource dataSource;

    public UnpooledDataSourceFactory() {
        this.dataSource = new UnpooledDataSource();
    }

    @Override
    public void setProperty(Properties properties) {
        Properties driverProperties = new Properties();
        MetaObject metaDataSource = SystemMetaObject.forObject(dataSource);

        for (Object key : properties.keySet()) {
            String propertyName = (String) key;
            if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) {
                // 数据库驱动属性
                String value = properties.getProperty(propertyName);
                driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
            } else if (metaDataSource.hasSetter(propertyName)) {
                // setter
                String value = (String) properties.getProperty(propertyName);
                Object convertedValue = convertValue(metaDataSource, propertyName, value);
                metaDataSource.setValue(propertyName, convertedValue);
            } else {
                throw new DataSourceException("Unknown DataSource property: " + propertyName);
            }
        }

        if (driverProperties.size() > 0) {
            metaDataSource.setValue("driverProperties", driverProperties);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 转换属性值
     *
     * @param metaDataSource 数据源元信息
     * @param propertyName   属性名
     * @param value          属性值
     * @return 转换后的属性值
     */
    private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
        Class<?> targetType = metaDataSource.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value);
        }

        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value);
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(value);
        }

        return value;
    }
}
