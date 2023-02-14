package com.pcz.mybatis.core.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常工具类
 *
 * @author picongzhi
 */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    /**
     * 解包 Throwable
     *
     * @param throwable Throwable
     * @return 结果
     */
    public static Throwable unwrapThrowable(Throwable throwable) {
        Throwable unwrapped = throwable;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }
}
