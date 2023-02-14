package com.pcz.mybatis.core.parsing;

/**
 * 通用的 Token 解析器
 *
 * @author picongzhi
 */
public class GenericTokenParser {
    /**
     * 开始 Token
     */
    private final String openToken;

    /**
     * 结束 Token
     */
    private final String closeToken;

    /**
     * Token 处理器
     */
    private final TokenHandler tokenHandler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler tokenHandler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.tokenHandler = tokenHandler;
    }

    /**
     * 解析
     *
     * @param text 输入字符串
     * @return 解析后的字符串
     */
    public String parse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 获取第一个开始 Token 的索引
        int startIndex = text.indexOf(openToken);
        if (startIndex == -1) {
            return text;
        }

        // 解析的结果
        final StringBuilder result = new StringBuilder();

        // 表达式
        StringBuilder expression = null;

        // 偏移位置
        int offset = 0;
        char[] chars = text.toCharArray();

        do {
            // 重置 expression
            if (expression == null) {
                expression = new StringBuilder();
            } else {
                expression.setLength(0);
            }

            // 添加结果，更新索引
            result.append(chars, offset, startIndex - offset);
            offset = startIndex + openToken.length();

            // 获取结束 Token 的索引
            int endIndex = text.indexOf(closeToken, offset);
            if (endIndex > -1) {
                expression.append(chars, offset, endIndex - offset);
                result.append(tokenHandler.handleToken(expression.toString()));
                offset = endIndex + closeToken.length();
            } else {
                result.append(chars, startIndex, chars.length - startIndex);
                offset = chars.length;
            }

            // 获取下一个开始 Token 的索引
            startIndex = text.indexOf(openToken, offset);
        } while (startIndex > -1);

        if (offset < chars.length) {
            result.append(chars, offset, chars.length - offset);
        }

        return result.toString();
    }
}
