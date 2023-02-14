package com.pcz.mybatis.core.type;

import java.lang.annotation.*;

/**
 * 映射的 Jdbc 类型
 *
 * @author picongzhi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappedJdbcTypes {
    /**
     * Jdbc 类型
     *
     * @return Jdbc 类型
     */
    JdbcType[] value();

    /**
     * 是否包含 null Jdbc 类型
     *
     * @return 是否包含 null Jdbc 类型
     */
    boolean includeNullJdbcType() default false;
}
