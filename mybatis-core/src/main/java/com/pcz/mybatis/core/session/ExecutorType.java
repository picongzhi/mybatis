package com.pcz.mybatis.core.session;

/**
 * 执行器类型
 *
 * @author picongzhi
 */
public enum ExecutorType {
    /**
     * 简单
     */
    SIMPLE,
    /**
     * 重用
     */
    REUSE,
    /**
     * 批量
     */
    BATCH;
}
