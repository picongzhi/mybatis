package com.pcz.mybatis.core.session;

/**
 * 本地缓存作用域
 *
 * @author picongzhi
 */
public enum LocalCacheScope {
    /**
     * 会话
     */
    SESSION,
    /**
     * 请求
     */
    STATEMENT;
}
