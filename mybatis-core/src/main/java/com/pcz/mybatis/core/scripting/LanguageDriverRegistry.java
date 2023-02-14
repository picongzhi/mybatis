package com.pcz.mybatis.core.scripting;

import com.pcz.mybatis.core.util.MapUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 语言驱动注册器
 *
 * @author picongzhi
 */
public class LanguageDriverRegistry {
    /**
     * 语言驱动类 -> 语言驱动实例 映射
     */
    private final Map<Class<? extends LanguageDriver>, LanguageDriver> LANGUAGE_DRIVER_MAP = new HashMap<>();

    /**
     * 默认的语言驱动 Class 实例
     */
    private Class<? extends LanguageDriver> defaultDriverClass;

    /**
     * 注册
     *
     * @param cls 语言驱动 Class 实例
     */
    public void register(Class<? extends LanguageDriver> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null is not a valid Language Driver");
        }

        MapUtil.compoteIfAbsent(LANGUAGE_DRIVER_MAP, cls, k -> {
            try {
                return k.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new ScriptingException(
                        "Failed to load language driver for " + cls.getName(), e);
            }
        });
    }

    /**
     * 注册
     *
     * @param driver 语言驱动实例
     */
    public void register(LanguageDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("null is not a valid Language Driver");
        }

        Class<? extends LanguageDriver> cls = driver.getClass();
        if (!LANGUAGE_DRIVER_MAP.containsKey(cls)) {
            LANGUAGE_DRIVER_MAP.put(cls, driver);
        }
    }

    /**
     * 获取语言驱动
     *
     * @param cls 语言驱动 Class 实例
     * @return 语言驱动实例
     */
    public LanguageDriver getDriver(Class<? extends LanguageDriver> cls) {
        return LANGUAGE_DRIVER_MAP.get(cls);
    }

    /**
     * 获取默认的语言驱动
     *
     * @return 默认的语言驱动
     */
    public LanguageDriver getDefaultDriver() {
        return getDriver(getDefaultDriverClass());
    }

    /**
     * 获取默认的语言驱动 Class 实例
     *
     * @return 默认的语言驱动 Class 实例
     */
    public Class<? extends LanguageDriver> getDefaultDriverClass() {
        return defaultDriverClass;
    }

    /**
     * 设置默认的语言驱动 Class 实例
     *
     * @param defaultDriverClass 默认的语言驱动 Class 实例
     */
    public void setDefaultDriverClass(Class<? extends LanguageDriver> defaultDriverClass) {
        register(defaultDriverClass);
        this.defaultDriverClass = defaultDriverClass;
    }
}
