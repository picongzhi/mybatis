package com.pcz.mybatis.core.binding;

import com.pcz.mybatis.core.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mapper 代理工厂
 *
 * @param <T> Mapper 泛型类
 * @author picongzhi
 */
public class MapperProxyFactory<T> {
    /**
     * Mapper 接口实例
     */
    private final Class<T> mapperInterface;

    /**
     * 方法缓存
     */
    private final Map<Method, MapperProxy.MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, MapperProxy.MapperMethodInvoker> getMethodCache() {
        return methodCache;
    }

    /**
     * 实例化 Mapper 代理
     *
     * @param mapperProxy MapperProxy
     * @return Mapper 代理
     */
    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(
                mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                mapperProxy);
    }

    /**
     * 实例化 Mapper 代理
     *
     * @param sqlSession Sql 会话
     * @return Mapper 代理
     */
    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(
                sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }
}
