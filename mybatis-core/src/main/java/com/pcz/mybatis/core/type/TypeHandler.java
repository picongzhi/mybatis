package com.pcz.mybatis.core.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理器
 *
 * @param <T> 类型泛型
 * @author picongzhi
 */
public interface TypeHandler<T> {
    /**
     * 设置参数
     *
     * @param preparedStatement 预编译语句
     * @param index             索引
     * @param parameter         参数
     * @param jdbcType          JdbcType
     * @throws SQLException SQL 异常
     */
    void setParameter(PreparedStatement preparedStatement,
                      int index,
                      T parameter,
                      JdbcType jdbcType) throws SQLException;

    /**
     * 获取结果
     *
     * @param resultSet  结果集
     * @param columnName 列名
     * @return 结果
     * @throws SQLException SQL 异常
     */
    T getResult(ResultSet resultSet, String columnName) throws SQLException;

    /**
     * 获取结果
     *
     * @param resultSet   结果集
     * @param columnIndex 列索引
     * @return 结果
     * @throws SQLException SQL 异常
     */
    T getResult(ResultSet resultSet, int columnIndex) throws SQLException;

    /**
     * 获取结果
     *
     * @param callableStatement 可执行的语句
     * @param columnIndex       列索引
     * @return 结果
     * @throws SQLException SQL 异常
     */
    T getResult(CallableStatement callableStatement, int columnIndex) throws SQLException;
}
