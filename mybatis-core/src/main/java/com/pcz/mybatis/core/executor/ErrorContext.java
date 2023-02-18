package com.pcz.mybatis.core.executor;

/**
 * Error 上下文
 *
 * @author picongzhi
 */
public class ErrorContext {
    /**
     * 行分隔符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 线上上下文 ErrorContext
     */
    private static final ThreadLocal<ErrorContext> LOCAL = ThreadLocal.withInitial(ErrorContext::new);

    /**
     * ErrorContext
     */
    private ErrorContext stored;

    /**
     * 资源路径
     */
    private String resource;

    /**
     * 行为
     */
    private String activity;

    /**
     * 对象
     */
    private String object;

    /**
     * 消息
     */
    private String message;

    /**
     * SQL
     */
    private String sql;

    /**
     * 根因
     */
    private Throwable cause;

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
     * 存储
     *
     * @return ErrorContext
     */
    public ErrorContext store() {
        ErrorContext errorContext = new ErrorContext();
        errorContext.stored = this;
        LOCAL.set(errorContext);

        return LOCAL.get();
    }

    /**
     * 撤回
     *
     * @return ErrorContext
     */
    public ErrorContext recall() {
        if (stored != null) {
            LOCAL.set(stored);
            stored = null;
        }

        return LOCAL.get();
    }

    /**
     * 重置
     *
     * @return ErrorContext
     */
    public ErrorContext reset() {
        resource = null;
        activity = null;
        object = null;
        message = null;
        sql = null;
        cause = null;

        LOCAL.remove();

        return this;
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

    /**
     * 设置对象
     *
     * @param object 对象
     * @return ErrorContext
     */
    public ErrorContext object(String object) {
        this.object = object;
        return this;
    }

    /**
     * 设置消息
     *
     * @param message 消息
     * @return ErrorContext
     */
    public ErrorContext message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 设置 SQL
     *
     * @param sql SQL
     * @return ErrorContext
     */
    public ErrorContext sql(String sql) {
        this.sql = sql;
        return this;
    }

    /**
     * 设置根因
     *
     * @param cause 根因
     * @return ErrorContext
     */
    public ErrorContext cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (message != null) {
            builder.append(LINE_SEPARATOR)
                    .append("### ")
                    .append(message);
        }

        if (resource != null) {
            builder.append(LINE_SEPARATOR)
                    .append("### The error may exist in ")
                    .append(resource);
        }

        if (object != null) {
            builder.append(LINE_SEPARATOR)
                    .append("### The error may involve ")
                    .append(object);
        }

        if (activity != null) {
            builder.append(LINE_SEPARATOR)
                    .append("### The error occurred while ")
                    .append(activity);
        }

        if (sql != null) {
            builder.append(LINE_SEPARATOR)
                    .append("### SQL: ")
                    .append(sql);
        }

        if (cause != null) {
            builder.append(LINE_SEPARATOR)
                    .append("### Cause: ")
                    .append(message);
        }

        return builder.toString();
    }
}
