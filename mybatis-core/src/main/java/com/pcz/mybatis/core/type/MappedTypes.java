package com.pcz.mybatis.core.type;

import java.lang.annotation.*;

/**
 * 映射的类型
 *
 * @author picongzhi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappedTypes {
    /**
     * 映射的类型
     *
     * @return 映射的类型
     */
    Class<?>[] value();
}
