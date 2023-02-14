package com.pcz.mybatis.core.session;

import com.pcz.mybatis.core.mapping.MappedStatement;

/**
 * 自动映射未知字段的行为
 *
 * @author picongzhi
 */
public enum AutoMappingUnknownColumnBehavior {
    /**
     * 什么都不做（默认）
     */
    NONE {
        @Override
        public void doAction(MappedStatement mappedStatement, String columnName, String propertyName, Class<?> propertyType) {
            // do nothing
        }
    },
    /**
     * 输出警告的日志
     */
    WARNING {
        @Override
        public void doAction(MappedStatement mappedStatement, String columnName, String propertyName, Class<?> propertyType) {
            // TODO: 打印警告日志
        }
    },
    /**
     * 映射失败
     */
    FAILING {
        @Override
        public void doAction(MappedStatement mappedStatement, String columnName, String propertyName, Class<?> propertyType) {
            throw new SqlSessionException(buildMessage(mappedStatement, columnName, propertyName, propertyType));
        }
    };

    /**
     * 执行
     *
     * @param mappedStatement 映射的语句
     * @param columnName      列名
     * @param propertyName    属性名
     * @param propertyType    属性类型
     */
    public abstract void doAction(MappedStatement mappedStatement, String columnName, String propertyName, Class<?> propertyType);

    /**
     * 构造消息
     *
     * @param mappedStatement 映射的语句
     * @param columnName      列名
     * @param propertyName    属性名
     * @param propertyType    属性类型
     * @return 消息
     */
    private static String buildMessage(MappedStatement mappedStatement, String columnName, String propertyName, Class<?> propertyType) {
        return new StringBuilder("Unknown column is detected on '")
                .append(mappedStatement.getId())
                .append("' auto-mapping. Mapping parameters are ")
                .append("[")
                .append("columnName=").append(columnName)
                .append(",").append("propertyName=").append(propertyName)
                .append(",").append("propertyType=").append(propertyType != null ? propertyType.getName() : null)
                .append("]")
                .toString();
    }
}
