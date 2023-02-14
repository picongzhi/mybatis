package com.pcz.mybatis.core.session;

/**
 * 结果处理器
 *
 * @param <T> 类型泛型
 */
public interface ResultHandler<T> {
    /**
     * 处理结果
     *
     * @param resultContext 结果上下文
     */
    void handleResult(ResultContext<? extends T> resultContext);
}
