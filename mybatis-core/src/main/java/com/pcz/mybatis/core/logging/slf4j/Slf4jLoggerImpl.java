package com.pcz.mybatis.core.logging.slf4j;

import com.pcz.mybatis.core.logging.Log;
import org.slf4j.Logger;

/**
 * Slf4j Logger 实现
 *
 * @author picongzhi
 */
public class Slf4jLoggerImpl implements Log {
    /**
     * log
     */
    private final Logger log;

    public Slf4jLoggerImpl(Logger logger) {
        this.log = logger;
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
