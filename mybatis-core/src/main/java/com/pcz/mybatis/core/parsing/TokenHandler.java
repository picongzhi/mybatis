package com.pcz.mybatis.core.parsing;

/**
 * Token 处理器
 *
 * @author picongzhi
 */
public interface TokenHandler {
    /**
     * 处理 Token
     *
     * @param content Token 内容
     * @return 处理后的值
     */
    String handleToken(String content);
}
