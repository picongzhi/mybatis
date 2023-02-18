package com.pcz.mybatis.core.logging.slf4j;

import com.pcz.mybatis.core.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Slf4j 实现
 *
 * @author picongzhi
 */
public class Slf4jImpl implements Log {
    /**
     * log
     */
    private Log log;

    public Slf4jImpl(String cls) {
        Logger logger = LoggerFactory.getLogger(cls);

        if (logger instanceof LocationAwareLogger) {
            // 尝试实例化 LocationAwareLogger
            try {
                logger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
                log = new Slf4jLocationAwareLoggerImpl((LocationAwareLogger) logger);
                return;
            } catch (SecurityException | NoSuchMethodException e) {
                // 忽略
            }
        }

        log = new Slf4jLoggerImpl(logger);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void error(String str, Throwable t) {
        log.error(str, t);
    }

    @Override
    public void error(String str) {
        log.error(str);
    }

    @Override
    public void warn(String str) {
        log.warn(str);
    }

    @Override
    public void debug(String str) {
        log.debug(str);
    }

    @Override
    public void trace(String str) {
        log.trace(str);
    }
}
