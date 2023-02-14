package com.pcz.mybatis.core.annotations;

import java.lang.annotation.*;

/**
 * 参数名注解
 *
 * @author picongzhi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    /**
     * 参数名
     *
     * @return 参数名
     */
    String value();
}
