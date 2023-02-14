package com.pcz.mybatis.core.executor;

/**
 * Error 上下文
 *
 * @author picongzhi
 */
public class ErrorContext {
    /**
     * 线上上下文 ErrorContext
     */
    private static final ThreadLocal<ErrorContext> LOCAL = ThreadLocal.withInitial(ErrorContext::new);

    /**
     * 资源路径
     */
    private String resource;

    /**
     * 行为
     */
    private String activity;

    private ErrorContext() {
    }

    /**
     * 获取单例
     *
     * @return ErrorContext
     */
    public static ErrorContext instance() {
        return LOCAL.get();
    }

    /**
     * 设置资源
     *
     * @param resource 资源路径
     * @return ErrorContext
     */
    public ErrorContext resource(String resource) {
        this.resource = resource;
        return this;
    }

    /**
     * 设置行为
     *
     * @param activity 行为
     * @return ErrorContext
     */
    public ErrorContext activity(String activity) {
        this.activity = activity;
        return this;
    }
}
