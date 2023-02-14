package com.pcz.mybatis.core.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法签名
 *
 * @author picongzhi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Signature {
    /**
     * Class 实例
     *
     * @return Class 实例
     */
    Class<?> type();

    /**
     * 方法名
     *
     * @return 方法名
     */
    String method();

    /**
     * 方法参数
     *
     * @return 方法参数
     */
    Class<?>[] args();
}
