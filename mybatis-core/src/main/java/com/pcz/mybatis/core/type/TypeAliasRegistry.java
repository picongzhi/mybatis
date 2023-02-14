package com.pcz.mybatis.core.type;

import com.pcz.mybatis.core.io.ResolveUtil;
import com.pcz.mybatis.core.io.Resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 类别名注册器
 *
 * @author picongzhi
 */
public class TypeAliasRegistry {
    private final Map<String, Class<?>> typeAlias = new HashMap<>();

    public TypeAliasRegistry() {
        registerAlias("string", String.class);
    }

    /**
     * 根据别名解析类实例
     *
     * @param alias 别名
     * @param <T>   类泛型
     * @return 类实例
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> resolveAlias(String alias) {
        if (alias == null) {
            return null;
        }

        // 先从别名注册中心获取
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (typeAlias.containsKey(key)) {
            return (Class<T>) typeAlias.get(key);
        }

        // 再根据类名加载类实例
        try {
            return (Class<T>) Resources.classForName(alias);
        } catch (ClassNotFoundException e) {
            throw new TypeException("Could not resolve type alias: '" + alias + "'. Cause: " + e, e);
        }
    }

    /**
     * 注册别名
     *
     * @param alias 别名
     * @param value 值
     */
    public void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            throw new TypeException("The parameter alias cannot be null");
        }

        String key = alias.toLowerCase(Locale.ENGLISH);
        if (typeAlias.containsKey(key) && typeAlias.get(key) != null && !typeAlias.get(key).equals(value)) {
            throw new TypeException("The alias '" + alias + "' is already mapped to the value '"
                    + typeAlias.get(key).getName() + "'");
        }

        typeAlias.put(key, value);
    }

    /**
     * 注册别名
     *
     * @param packageName 包名
     */
    public void registerAliases(String packageName) {
        registerAliases(packageName, Object.class);
    }

    /**
     * 注册别名
     *
     * @param packageName 包名
     * @param superType   父类
     */
    public void registerAliases(String packageName, Class<?> superType) {
        ResolveUtil<Class<?>> resolveUtil = new ResolveUtil<>();
        resolveUtil.find(new ResolveUtil.IsA(superType), packageName);

        Set<Class<? extends Class<?>>> classes = resolveUtil.getClasses();
        for (Class<?> cls : classes) {
            if (!cls.isAnonymousClass() && !cls.isInterface() && !cls.isMemberClass()) {
                registerAlias(cls);
            }
        }
    }

    /**
     * 注册别名
     *
     * @param cls Class 实例
     */
    public void registerAlias(Class<?> cls) {
        String alias = cls.getSimpleName();
        // TODO: 实现 Alias 注解
        registerAlias(alias, cls);
    }
}
