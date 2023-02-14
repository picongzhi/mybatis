package com.pcz.mybatis.core.parsing;

import java.util.Properties;

/**
 * 属性解析器
 *
 * @author picongzhi
 */
public class PropertyParser {
    /**
     * key 前缀
     */
    private static final String KEY_PREFIX = "com.pcz.mybatis.core.parsing.PropertyParser.";

    /**
     * 启用默认值 key
     */
    public static final String KEY_ENABLE_DEFAULT_VALUE = KEY_PREFIX + "enable-default-value";

    /**
     * 默认值分隔符 key
     */
    public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";

    /**
     * 启用默认值
     */
    private static final String ENABLE_DEFAULT_VALUE = "false";

    /**
     * 默认值分隔符
     */
    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    private PropertyParser() {
    }

    /**
     * 解析属性
     *
     * @param value     输入
     * @param variables 变量
     * @return 解析结果
     */
    public static String parse(String value, Properties variables) {
        VariableTokenHandler tokenHandler = new VariableTokenHandler(variables);
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", tokenHandler);

        return tokenParser.parse(value);
    }

    /**
     * 变量 Token 处理器
     */
    private static class VariableTokenHandler implements TokenHandler {
        /**
         * 属性
         */
        private final Properties variables;

        /**
         * 是否启用默认值
         */
        private final boolean enableDefaultValue;

        /**
         * 默认值分隔符
         */
        private final String defaultValueSeparator;

        private VariableTokenHandler(Properties variables) {
            this.variables = variables;
            this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFAULT_VALUE, ENABLE_DEFAULT_VALUE));
            this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
        }

        @Override
        public String handleToken(String content) {
            String result = "${" + content + "}";
            if (variables == null) {
                // 变量为 null
                return result;
            }

            String key = content;
            if (enableDefaultValue) {
                // 获取 key 和 默认值的分隔符索引
                final int separatorIndex = content.indexOf(defaultValueSeparator);

                String defaultValue = null;
                if (separatorIndex >= 0) {
                    // 索引存在
                    key = content.substring(0, separatorIndex);
                    defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());

                    return variables.getProperty(key, defaultValue);
                } else {
                    // 索引不存在
                    return variables.getProperty(key);
                }
            }

            if (variables.containsKey(key)) {
                return variables.getProperty(key);
            }

            return result;
        }

        /**
         * 获取属性值
         *
         * @param key          属性 key
         * @param defaultValue 默认值
         * @return 属性值
         */
        private String getPropertyValue(String key, String defaultValue) {
            return variables == null ?
                    defaultValue : variables.getProperty(key, defaultValue);
        }
    }
}
