package com.pcz.mybatis.core.annotations;

import java.lang.annotation.*;

/**
 * 配置属性或列值对应的 Map key
 *
 * @author picongzhi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapKey {
    /**
     * 属性或列值对应的 Map key
     *
     * @return 属性或列值对应的 Map key
     */
    String value();
}
