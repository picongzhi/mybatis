package com.pcz.mybatis.core.type;

import com.pcz.mybatis.core.executor.loader.result.ResultMapException;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 根类型处理器
 *
 * @param <T> 泛型
 * @author picongzhi
 */
public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int index, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            if (jdbcType == null) {
                throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
            }

            try {
                preparedStatement.setNull(index, jdbcType.TYPE_CODE);
            } catch (SQLException e) {
                throw new TypeException("Error setting null for parameter #" + index + " with JdbcType " + jdbcType + ". "
                        + "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. "
                        + "Cause: " + e, e);
            }
        } else {
            try {
                setNonNullParameter(preparedStatement, index, parameter, jdbcType);
            } catch (Exception e) {
                throw new TypeException("Error setting non null for parameter #" + index + " with JdbcType " + jdbcType + ". "
                        + "Try setting a different JdbcType for this parameter or a different configuration property. "
                        + "Cause: " + e, e);
            }
        }
    }

    @Override
    public T getResult(ResultSet resultSet, String columnName) throws SQLException {
        try {
            return getNullableResult(resultSet, columnName);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column '" + columnName
                    + "' from result set. Cause: " + e, e);
        }
    }

    @Override
    public T getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        try {
            return getNullableResult(resultSet, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column #" + columnIndex
                    + " from result set. Cause: " + e, e);
        }
    }

    @Override
    public T getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        try {
            return getNullableResult(callableStatement, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column '" + columnIndex
                    + "' from callable statement. Cause: " + e, e);
        }
    }

    /**
     * 设置非空参数
     *
     * @param preparedStatement 预编译语句
     * @param index             参数索引
     * @param parameter         参数值
     * @param jdbcType          JdbcType
     * @throws SQLException SQL 异常
     */
    public abstract void setNonNullParameter(PreparedStatement preparedStatement, int index, T parameter, JdbcType jdbcType) throws SQLException;

    /**
     * 获取结果（可为空）
     *
     * @param resultSet  结果集
     * @param columnName 列名
     * @return 结果
     * @throws SQLException SQL 异常
     */
    public abstract T getNullableResult(ResultSet resultSet, String columnName) throws SQLException;

    /**
     * 获取结果（可为空）
     *
     * @param resultSet   结果集
     * @param columnIndex 列索引
     * @return 结果
     * @throws SQLException SQL 异常
     */
    public abstract T getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException;

    /**
     * 获取结果（可为空）
     *
     * @param callableStatement 可执行语句
     * @param columnIndex       列索引
     * @return 结果
     * @throws SQLException SQL 异常
     */
    public abstract T getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException;
}
