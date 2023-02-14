package com.pcz.mybatis.core.logging;

/**
 * 日志接口
 *
 * @author picongzhi
 */
public interface Log {
    /**
     * 判断是否启用 debug 级别的日志
     *
     * @return 是否启用 debug 级别的日志
     */
    boolean isDebugEnabled();

    /**
     * 判断是否启用 trace 级别的日志
     *
     * @return 是否启用 trace 级别的日志
     */
    boolean isTraceEnabled();

    /**
     * 打印 error 级别的日志
     *
     * @param str 内容
     * @param t   异常
     */
    void error(String str, Throwable t);

    /**
     * 打印 error 级别的日志
     *
     * @param str 内容
     */
    void error(String str);

    /**
     * 打印 warn 级别的日志
     *
     * @param str 内容
     */
    void warn(String str);

    /**
     * 打印 debug 级别的日志
     *
     * @param str 内容
     */
    void debug(String str);

    /**
     * 打印 trace 级别的日志
     *
     * @param str 内容
     */
    void trace(String str);
}
