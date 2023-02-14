package com.pcz.mybatis.core.session;

import com.pcz.mybatis.core.cursor.Cursor;
import com.pcz.mybatis.core.executor.BatchResult;

import java.util.List;
import java.util.Map;

/**
 * Sql 会话
 *
 * @author picongzhi
 */
public interface SqlSession {
    /**
     * 获取 Mapper
     *
     * @param cls Mapper Class 实例
     * @param <T> 泛型
     * @return Mapper 实例
     */
    <T> T getMapper(Class<T> cls);

    /**
     * 获取配置
     *
     * @return 配置
     */
    Configuration getConfiguration();

    /**
     * 执行 insert 语句
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @return 影响行数
     */
    int insert(String statement, Object parameter);

    /**
     * 执行 update 语句
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @return 影响行数
     */
    int update(String statement, Object parameter);

    /**
     * 执行 delete 语句
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @return 影响行数
     */
    int delete(String statement, Object parameter);

    /**
     * 执行 select 语句，获取一行记录
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @param <T>       映射的对象泛型
     * @return 映射的对象
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * 取出映射的多行记录
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @param <E>       映射的对象泛型
     * @return 映射的集合
     */
    <E> List<E> selectList(String statement, Object parameter);


    /**
     * 取出映射的多行记录
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @param rowBounds 行边界
     * @param <E>       映射的对象泛型
     * @return 映射的集合
     */
    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

    /**
     * 将结果集根据属性转成 Map
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @param mapKey    映射的 key
     * @param <K>       key 泛型
     * @param <V>       value 泛型
     * @return Map
     */
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

    /**
     * 将结果集根据属性转成 Map
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @param mapKey    映射的 key
     * @param rowBounds 行边界
     * @param <K>       key 泛型
     * @param <V>       value 泛型
     * @return Map
     */
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

    /**
     * 将结果集转 List，并支持懒加载
     *
     * @param statement 语句
     * @param parameter 参数对象
     * @param <T>       泛型
     * @return 游标
     */
    <T> Cursor<T> selectCursor(String statement, Object parameter);

    /**
     * 将结果集转 List，并支持懒加载
     *
     * @param statement 语句
     * @param parameter 参数
     * @param rowBounds 行边界
     * @param <T>       泛型
     * @return 游标
     */
    <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

    /**
     * 取出映射的单行记录
     *
     * @param statement     语句
     * @param parameter     参数对象
     * @param resultHandler 结果处理器
     */
    void select(String statement, Object parameter, ResultHandler resultHandler);

    /**
     * 取出映射的单行记录
     *
     * @param statement     语句
     * @param parameter     参数对象
     * @param rowBounds     行边界
     * @param resultHandler 结果处理器
     */
    void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler);

    /**
     * 批量刷新语句
     *
     * @return 批量更新结果
     */
    List<BatchResult> flushStatements();
}
