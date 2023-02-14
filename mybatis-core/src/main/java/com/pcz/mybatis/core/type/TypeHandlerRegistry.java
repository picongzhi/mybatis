package com.pcz.mybatis.core.type;

import com.pcz.mybatis.core.io.ResolveUtil;
import com.pcz.mybatis.core.session.Configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型处理器注册器
 *
 * @author picongzhi
 */
public final class TypeHandlerRegistry {
    /**
     * 空的类型转换器
     */
    private static final Map<JdbcType, TypeHandler<?>> NULL_TYPE_HANDLER_MAP = Collections.emptyMap();

    /**
     * 类型转换器
     * JavaType -> (JdbcType -> TypeHandler)
     */
    private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();

    /**
     * 所有类型映射器
     * JavaType -> TypeHandler
     */
    private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();

    /**
     * 默认的枚举类型处理器 Class 实例
     */
    private Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;

    public TypeHandlerRegistry() {
        this(new Configuration());
    }

    public TypeHandlerRegistry(Configuration configuration) {

    }

    /**
     * 设置默认的枚举类型处理器
     *
     * @param typeHandler 类型处理器
     */
    public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandler) {
        this.defaultEnumTypeHandler = typeHandler;
    }

    /**
     * 注册指定包下的所有类型处理器
     *
     * @param packageName 包名
     */
    public void register(String packageName) {
        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.find(new ResolveUtil.IsA(TypeHandler.class), packageName);

        Set<Class<? extends Class<?>>> handlerClasses = resolveUtil.getClasses();
        for (Class<?> handlerCls : handlerClasses) {
            if (!handlerCls.isAnonymousClass()
                    && !handlerCls.isInterface()
                    && !Modifier.isAbstract(handlerCls.getModifiers())) {
                register(handlerCls);
            }
        }
    }

    /**
     * 注册类型处理器
     *
     * @param typeHandlerClass 类型处理器 Class 实例
     */
    public void register(Class<?> typeHandlerClass) {
        boolean mappedTypeFound = false;
        MappedTypes mappedTypes = typeHandlerClass.getAnnotation(MappedTypes.class);
        if (mappedTypes != null) {
            for (Class<?> mappedType : mappedTypes.value()) {
                register(mappedType, typeHandlerClass);
                mappedTypeFound = true;
            }
        }

        if (!mappedTypeFound) {
            register(getInstance(null, typeHandlerClass));
        }
    }

    /**
     * 注册类型转换器
     *
     * @param typeHandler 类型转换器
     * @param <T>         类型泛型
     */
    @SuppressWarnings("unchecked")
    public <T> void register(TypeHandler<T> typeHandler) {
        boolean mappedTypeFound = false;
        MappedTypes mappedTypes = typeHandler.getClass().getAnnotation(MappedTypes.class);
        if (mappedTypes != null) {
            for (Class<?> mappedType : mappedTypes.value()) {
                register(mappedType, typeHandler);
                mappedTypeFound = true;
            }
        }

        if (!mappedTypeFound && typeHandler instanceof TypeReference) {
            try {
                TypeReference<T> typeReference = (TypeReference<T>) typeHandler;
                register(typeReference.getRawType(), typeHandler);
                mappedTypeFound = true;
            } catch (Throwable t) {
            }
        }

        if (!mappedTypeFound) {
            register((Class<T>) null, typeHandler);
        }
    }

    /**
     * 注册类型处理器
     *
     * @param javaTypeClass    Java 类型 Class 实例
     * @param typeHandlerClass 类型处理器 Class 实例
     */
    public void register(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        register(javaTypeClass, getInstance(javaTypeClass, typeHandlerClass));
    }

    /**
     * 注册类型处理器
     *
     * @param javaTypeClass    Java 类型 Class 实例
     * @param jdbcType         Jdbc 类型
     * @param typeHandlerClass 类型处理器 Class 实例
     */
    public void register(Class<?> javaTypeClass, JdbcType jdbcType, Class<?> typeHandlerClass) {
        register(javaTypeClass, jdbcType, getInstance(javaTypeClass, typeHandlerClass));
    }

    /**
     * 注册类型处理器
     *
     * @param javaTypeClass Java 类型 Class 实例
     * @param typeHandler   类型处理器实例
     * @param <T>           类型泛型
     */
    public <T> void register(Class<T> javaTypeClass, TypeHandler<? extends T> typeHandler) {
        register((Type) javaTypeClass, typeHandler);
    }

    /**
     * 注册类型处理器
     *
     * @param javaType    Java 类型
     * @param typeHandler 类型处理器实例
     * @param <T>         类型泛型
     */
    private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
        MappedJdbcTypes mappedJdbcTypes = typeHandler.getClass().getAnnotation(MappedJdbcTypes.class);
        if (mappedJdbcTypes != null) {
            for (JdbcType jdbcType : mappedJdbcTypes.value()) {
                register(javaType, jdbcType, typeHandler);
            }

            if (mappedJdbcTypes.includeNullJdbcType()) {
                register(javaType, null, typeHandler);
            }
        } else {
            register(javaType, null, typeHandler);
        }
    }

    /**
     * 注册类型处理器
     *
     * @param javaType    Java 类型
     * @param jdbcType    Jdbc 类型
     * @param typeHandler 类型处理器
     */
    private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> typeHandler) {
        if (javaType != null) {
            Map<JdbcType, TypeHandler<?>> jdbcTypeHandlers = typeHandlerMap.get(javaType);
            if (jdbcTypeHandlers == null || jdbcTypeHandlers == NULL_TYPE_HANDLER_MAP) {
                jdbcTypeHandlers = new HashMap<>();
            }

            jdbcTypeHandlers.put(jdbcType, typeHandler);
            typeHandlerMap.put(javaType, jdbcTypeHandlers);
        }

        allTypeHandlersMap.put(typeHandler.getClass(), typeHandler);
    }

    /**
     * 获取类型处理器实例
     *
     * @param javaTypeClass    Java 类型 Class 实例
     * @param typeHandlerClass 类型处理器 Class 实例
     * @param <T>              Java 类型泛型
     * @return 类型处理器实例
     */
    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            // Java 类型 Class 实例不为 null
            try {
                Constructor<?> constructor = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) constructor.newInstance(javaTypeClass);
            } catch (NoSuchMethodException e) {
                // 忽略
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }

        try {
            Constructor<?> constructor = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) constructor.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }
}
