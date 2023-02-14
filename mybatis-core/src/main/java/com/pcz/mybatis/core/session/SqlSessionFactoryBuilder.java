package com.pcz.mybatis.core.session;

import com.pcz.mybatis.core.builder.xml.XMLConfigBuilder;

import java.io.Reader;

/**
 * SqlSessionFactory 构造器
 *
 * @author picongzhi
 * @see SqlSessionFactory
 */
public class SqlSessionFactoryBuilder {
    /**
     * 构造 SqlSessionFactory
     *
     * @param reader Reader
     * @return SqlSessionFactory
     */
    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder builder = new XMLConfigBuilder(reader);
        Configuration configuration = builder.parse();

        return build(configuration);
    }

    /**
     * 构造 SqlSessionFactory
     *
     * @param configuration 配置
     * @return SqlSessionFactory
     */
    public SqlSessionFactory build(Configuration configuration) {
        return null;
    }
}
