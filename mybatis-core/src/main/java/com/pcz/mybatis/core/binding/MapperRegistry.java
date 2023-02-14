package com.pcz.mybatis.core.binding;

import com.pcz.mybatis.core.builder.annotation.MapperAnnotationBuilder;
import com.pcz.mybatis.core.io.ResolveUtil;
import com.pcz.mybatis.core.session.Configuration;
import com.pcz.mybatis.core.session.SqlSession;

import java.util.*;

/**
 * Mapper 注册器
 *
 * @author picongzhi
 */
public class MapperRegistry {
    /**
     * 配置
     */
    private final Configuration configuration;

    /**
     * 已知的 Mapper
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取 Mapper
     *
     * @param cls        Mapper Class 实例
     * @param sqlSession SQL 会话
     * @param <T>        Mapper 泛型
     * @return Mapper
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> cls, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(cls);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + cls + " is not known to the MapperRegistry.");
        }

        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    /**
     * 获取 Mapper
     *
     * @return Mapper
     */
    public Collection<Class<?>> getMappers() {
        return Collections.unmodifiableCollection(knownMappers.keySet());
    }

    /**
     * 判断 Mapper 是否存在
     *
     * @param cls Mapper Class 实例
     * @param <T> Mapper 泛型
     * @return 是否存在
     */
    public <T> boolean hasMapper(Class<T> cls) {
        return knownMappers.containsKey(cls);
    }

    /**
     * 添加 Mapper
     *
     * @param cls Mapper Class 实例
     * @param <T> Mapper 泛型
     */
    public <T> void addMapper(Class<T> cls) {
        if (!cls.isInterface()) {
            return;
        }

        if (hasMapper(cls)) {
            throw new BindingException("Type " + cls + " is already known to the MapperRegistry.");
        }

        boolean loadCompleted = false;
        try {
            knownMappers.put(cls, new MapperProxyFactory<>(cls));
            MapperAnnotationBuilder builder = new MapperAnnotationBuilder(configuration, cls);
            builder.parse();
            loadCompleted = true;
        } finally {
            if (!loadCompleted) {
                knownMappers.remove(cls);
            }
        }
    }

    /**
     * 添加 Mapper
     *
     * @param packageName 包名
     */
    public void addMappers(String packageName) {
        addMappers(packageName, Object.class);
    }

    /**
     * 添加 Mapper
     *
     * @param packageName 包名
     * @param superClass  父类
     */
    public void addMappers(String packageName, Class<?> superClass) {
        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.find(new ResolveUtil.IsA(superClass), packageName);

        Set<Class<? extends Class<?>>> mapperClasses = resolveUtil.getClasses();
        for (Class<?> mapperClass : mapperClasses) {
            addMapper(mapperClass);
        }
    }
}
