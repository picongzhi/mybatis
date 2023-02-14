package com.pcz.mybatis.core.session;

/**
 * Sql 会话工厂
 *
 * @author picongzhi
 */
public interface SqlSessionFactory {
    /**
     * 开启会话
     *
     * @return SqlSession
     */
    SqlSession openSession();
}
