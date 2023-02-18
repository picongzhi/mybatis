package com.pcz.mybatis.core.logging;

import com.pcz.mybatis.core.logging.slf4j.Slf4jImpl;

import java.lang.reflect.Constructor;

/**
 * 日志工厂
 *
 * @author picongzhi
 */
public final class LogFactory {
    /**
     * 标记
     */
    public static final String MARKER = "MYBATIS";

    /**
     * 日志构造器
     */
    private static Constructor<? extends Log> logConstructor;

    static {
        tryImplementation(LogFactory::useSlf4jLogging);
    }

    /**
     * 使用自定义日志
     *
     * @param cls 日志 Class 实例
     */
    public static synchronized void useCustomLogging(Class<? extends Log> cls) {
        // TODO: 实现日志模块
    }

    public static Log getLog(Class<?> cls) {
        return getLog(cls.getName());
    }

    public static Log getLog(String logger) {
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ". Cause: " + t, t);
        }
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(Slf4jImpl.class);
    }

    /**
     * 尝试实现
     *
     * @param runnable Runnable
     */
    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                // 忽略
            }
        }
    }

    /**
     * 设置实现
     *
     * @param implClass 实现类
     */
    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
            Log log = candidate.newInstance(LogFactory.class.getName());
            if (log.isDebugEnabled()) {
                log.debug("Logging initialized using '" + implClass + "' apadter.");
            }

            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation. Cause: " + t, t);
        }
    }
}
