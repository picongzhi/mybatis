package com.pcz.mybatis.core.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Jdbc 类型
 *
 * @author picongzhi
 */
public enum JdbcType {
    /**
     * VARCHAR
     */
    VARCHAR(Types.VARCHAR),
    /**
     * 其他
     */
    OTHER(Types.OTHER),
    /**
     * NULL
     */
    NULL(Types.NULL);

    public final int TYPE_CODE;

    /**
     * code 查找表
     */
    private final static Map<Integer, JdbcType> CODE_LOOK_UP = new HashMap<>();

    static {
        for (JdbcType type : JdbcType.values()) {
            CODE_LOOK_UP.put(type.TYPE_CODE, type);
        }
    }

    JdbcType(int code) {
        this.TYPE_CODE = code;
    }

    /**
     * 根据 code 获取 JdbcType
     *
     * @param code code
     * @return JdbcType
     */
    public static JdbcType forCode(int code) {
        return CODE_LOOK_UP.get(code);
    }
}
