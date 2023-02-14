package com.pcz.mybatis.core.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举类型处理器
 *
 * @param <E> 枚举泛型
 * @author picongzhi
 */
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    /**
     * 枚举类型
     */
    private final Class<E> type;

    public EnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int index, E parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            preparedStatement.setString(index, parameter.name());
        } else {
            preparedStatement.setObject(index, parameter.name(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String result = resultSet.getString(columnName);
        return result == null
                ? null
                : Enum.valueOf(type, result);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String result = resultSet.getString(columnIndex);
        return result == null
                ? null
                : Enum.valueOf(type, result);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String result = callableStatement.getString(columnIndex);
        return result == null
                ? null
                : Enum.valueOf(type, result);
    }
}
