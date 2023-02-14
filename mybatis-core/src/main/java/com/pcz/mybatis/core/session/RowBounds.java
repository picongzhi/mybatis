package com.pcz.mybatis.core.session;

/**
 * 行边界
 *
 * @author picongzhi
 */
public class RowBounds {
    /**
     * 空行偏移
     */
    public static final int NO_ROW_OFFSET = 0;

    /**
     * 空行限制
     */
    public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;

    /**
     * 默认值
     */
    public static final RowBounds DEFAULT = new RowBounds();

    /**
     * 偏移
     */
    private final int offset;

    /**
     * 限制
     */
    private final int limit;

    public RowBounds() {
        this(NO_ROW_OFFSET, NO_ROW_LIMIT);
    }

    public RowBounds(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
