package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.mapping.ParameterMode;
import com.pcz.mybatis.core.mapping.ResultSetType;
import com.pcz.mybatis.core.session.Configuration;
import com.pcz.mybatis.core.type.JdbcType;
import com.pcz.mybatis.core.type.TypeAliasRegistry;
import com.pcz.mybatis.core.type.TypeHandlerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 基本构造器
 *
 * @author picongzhi
 */
public abstract class BaseBuilder {
    /**
     * 配置
     */
    protected final Configuration configuration;

    /**
     * 类型别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry;

    /**
     * 类型处理器注册器
     */
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    }

    /**
     * 解析类实例
     *
     * @param className 类名
     * @param <T>       类泛型
     * @return 类实例
     */
    protected <T> Class<? extends T> resolveClass(String className) {
        if (className == null) {
            return null;
        }

        try {
            return resolveAlias(className);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    /**
     * 解析别名
     *
     * @param alias 别名
     * @param <T>   类泛型
     * @return 类实例
     */
    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

    /**
     * 获取 boolean 值
     *
     * @param value        输入值
     * @param defaultValue 默认值
     * @return boolean 值
     */
    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    /**
     * 获取 Integer 值
     *
     * @param value        输入值
     * @param defaultValue 默认值
     * @return Integer 值
     */
    protected Integer integerValueOf(String value, Integer defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    /**
     * 获取 Set<String> 值
     *
     * @param value        输入值
     * @param defaultValue 默认值
     * @return Set<String> 值
     */
    protected Set<String> stringSetValueOf(String value, String defaultValue) {
        value = value == null ? defaultValue : value;
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

    /**
     * 解析结果集类型
     *
     * @param alias 别名
     * @return 结果集类型
     */
    protected ResultSetType resolveResultSetType(String alias) {
        if (alias == null) {
            return null;
        }

        try {
            return ResultSetType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving ResultSetType. Cause: " + e, e);
        }
    }

    /**
     * 解析 Jdbc 类型
     *
     * @param alias 类型名
     * @return JdbcType
     */
    protected JdbcType resolveJdbcType(String alias) {
        if (alias == null) {
            return null;
        }

        try {
            return JdbcType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
        }
    }

    /**
     * 解析 ParameterMode
     *
     * @param alias 模式名
     * @return ParameterMode
     */
    protected ParameterMode resolveParameterMode(String alias) {
        if (alias == null) {
            return null;
        }

        try {
            return ParameterMode.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving ParameterMode. Cause: " + e, e);
        }
    }

    /**
     * 创建实例
     *
     * @param alias 别名
     * @return 实例
     */
    protected Object createInstance(String alias) {
        Class<?> cls = resolveClass(alias);
        if (cls == null) {
            return null;
        }

        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BuilderException("Error creating instance. Cause: " + e, e);
        }
    }
}
