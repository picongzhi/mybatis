package com.pcz.mybatis.core.session;

/**
 * 结果上下文
 *
 * @param <T> 结果泛型
 * @author picongzhi
 */
public interface ResultContext<T> {
    /**
     * 获取结果对象
     *
     * @return 结果对象
     */
    T getResultObject();

    /**
     * 获取结果数
     *
     * @return 结果数
     */
    int getResultCount();

    /**
     * 判断是否停止
     *
     * @return 是否停止
     */
    boolean isStopped();

    /**
     * 停止
     */
    void stop();
}
