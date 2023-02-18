package com.pcz.mybatis.core.builder;

/**
 * 初始化对象
 *
 * @author picongzhi
 */
public interface InitializingObject {
    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    void initialize() throws Exception;
}
