package com.pcz.mybatis.core.logging.slf4j;

import com.pcz.mybatis.core.logging.Log;

/**
 * Slf4j 实现
 *
 * @author picongzhi
 */
public class Slf4jImpl implements Log {
    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String str, Throwable t) {

    }

    @Override
    public void error(String str) {

    }

    @Override
    public void warn(String str) {

    }

    @Override
    public void debug(String str) {

    }

    @Override
    public void trace(String str) {

    }
}
