package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.annotations.Param;
import com.pcz.mybatis.core.binding.MapperMethod;
import com.pcz.mybatis.core.session.Configuration;
import com.pcz.mybatis.core.session.ResultHandler;
import com.pcz.mybatis.core.session.RowBounds;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 参数名解析器
 *
 * @author picongzhi
 */
public class ParamNameResolver {
    /**
     * 通用名称前缀
     */
    public static final String GENERIC_NAME_PREFIX = "param";

    /**
     * 是否使用实际的参数名
     */
    private final boolean useActualParamName;

    /**
     * key 为参数索引
     * value 为参数名
     */
    private final SortedMap<Integer, String> names;

    /**
     * 是有有 Param 注解
     */
    private boolean hasParamAnnotation;

    public ParamNameResolver(Configuration configuration, Method method) {
        this.useActualParamName = configuration.isUseActualParamName();

        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<>();

        int paramCount = paramAnnotations.length;
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            if (isSpecialParameter(paramTypes[paramIndex])) {
                continue;
            }

            String name = null;
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }

            if (name == null) {
                if (useActualParamName) {
                    name = getActualParamName(method, paramIndex);
                }

                if (name == null) {
                    name = String.valueOf(map.size());
                }
            }

            map.put(paramIndex, name);
        }

        names = Collections.unmodifiableSortedMap(map);
    }

    /**
     * 获取命名的参数
     *
     * @param args 参数
     * @return 命名的参数
     */
    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            return null;
        }

        if (!hasParamAnnotation && paramCount == 1) {
            Object value = args[names.firstKey()];
            return wrapToMapIfCollection(value,
                    useActualParamName ? names.get(names.firstKey()) : null);
        }

        final Map<String, Object> param = new MapperMethod.ParamMap<>();
        int i = 0;
        for (Map.Entry<Integer, String> entry : names.entrySet()) {
            param.put(entry.getValue(), args[entry.getKey()]);
            final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
            if (!names.containsValue(genericParamName)) {
                param.put(genericParamName, args[entry.getKey()]);
            }
            i++;
        }

        return param;
    }

    /**
     * 如果是 {@link java.util.Collection} 或 Array，包装成 Map
     *
     * @param object          对象
     * @param actualParamName 实际参数名
     * @return 包装后的对象
     */
    public static Object wrapToMapIfCollection(Object object, String actualParamName) {
        if (object instanceof Collection) {
            MapperMethod.ParamMap<Object> paramMap = new MapperMethod.ParamMap<>();
            paramMap.put("collection", object);
            if (object instanceof List) {
                paramMap.put("list", object);
            }

            Optional.ofNullable(actualParamName)
                    .ifPresent(name -> paramMap.put(name, object));

            return paramMap;
        }

        if (object != null && object.getClass().isArray()) {
            MapperMethod.ParamMap<Object> paramMap = new MapperMethod.ParamMap<>();
            paramMap.put("array", object);
            Optional.ofNullable(actualParamName)
                    .ifPresent(name -> paramMap.put(name, object));

            return paramMap;
        }

        return object;
    }

    /**
     * 判断是否是特殊参数
     * {@link RowBounds} 或 {@link ResultHandler}
     *
     * @param cls Class 实例
     * @return 是否是特殊参数
     */
    private static boolean isSpecialParameter(Class<?> cls) {
        return RowBounds.class.isAssignableFrom(cls)
                || ResultHandler.class.isAssignableFrom(cls);
    }

    /**
     * 获取实际的参数名
     *
     * @param method     方法名
     * @param paramIndex 参数索引
     * @return 参数名
     */
    private String getActualParamName(Method method, int paramIndex) {
        return ParamNameUtil.getParamNames(method).get(paramIndex);
    }
}
