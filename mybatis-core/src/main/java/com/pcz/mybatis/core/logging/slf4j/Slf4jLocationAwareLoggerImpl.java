package com.pcz.mybatis.core.logging.slf4j;

import com.pcz.mybatis.core.logging.Log;
import com.pcz.mybatis.core.logging.LogFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * 感知路径的 Slf4j Logger
 *
 * @author picongzhi
 */
public class Slf4jLocationAwareLoggerImpl implements Log {
    /**
     * 标记
     */
    private static final Marker MARKER = MarkerFactory.getMarker(LogFactory.MARKER);

    /**
     * 日志类全命名
     */
    private static final String FQCN = Slf4jImpl.class.getName();

    /**
     * logger
     */
    private final LocationAwareLogger logger;

    Slf4jLocationAwareLoggerImpl(LocationAwareLogger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void error(String str, Throwable t) {
        logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, str, null, t);
    }

    @Override
    public void error(String str) {
        logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, str, null, null);
    }

    @Override
    public void warn(String str) {
        logger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, str, null, null);
    }

    @Override
    public void debug(String str) {
        logger.log(MARKER, FQCN, LocationAwareLogger.DEBUG_INT, str, null, null);
    }

    @Override
    public void trace(String str) {
        logger.log(MARKER, FQCN, LocationAwareLogger.TRACE_INT, str, null, null);
    }
}
