package com.pcz.mybatis.core.session;

/**
 * 自动映射行为
 * 映射 column 到 field
 *
 * @author picongzhi
 */
public enum AutoMappingBehavior {
    /**
     * 无映射
     */
    NONE,
    /**
     * 部分映射，只映射没有嵌套的结果
     */
    PARTIAL,
    /**
     * 全部映射，包括复杂的嵌套
     */
    FULL;
}
