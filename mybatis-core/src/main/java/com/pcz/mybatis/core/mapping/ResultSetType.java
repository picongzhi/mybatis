package com.pcz.mybatis.core.mapping;

import java.sql.ResultSet;

/**
 * 结果集类型
 *
 * @author picongzhi
 */
public enum ResultSetType {
    /**
     * 默认
     */
    DEFAULT(-1),
    /**
     * 结果集指针只向前移动
     */
    FORWARD_ONLY(ResultSet.TYPE_FORWARD_ONLY),
    /**
     * 结果集指针可滚动，但是对结果集的数据更改不敏感
     */
    SCROLL_INSENSITIVE(ResultSet.TYPE_SCROLL_INSENSITIVE),
    /**
     * 结果集指针可滚动，但是对结果集的数据更改敏感
     */
    SCROLL_SENSITIVE(ResultSet.TYPE_SCROLL_SENSITIVE);

    /**
     * 类型值
     */
    private final int value;

    ResultSetType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
