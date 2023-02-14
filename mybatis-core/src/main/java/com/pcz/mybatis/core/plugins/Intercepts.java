package com.pcz.mybatis.core.plugins;

import java.lang.annotation.*;

/**
 * 拦截器
 *
 * @author picongzhi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {
    /**
     * 方法签名
     *
     * @return 方法签名
     */
    Signature[] value();
}
