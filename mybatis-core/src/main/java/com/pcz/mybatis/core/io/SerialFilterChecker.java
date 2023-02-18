package com.pcz.mybatis.core.io;

import com.pcz.mybatis.core.logging.Log;
import com.pcz.mybatis.core.logging.LogFactory;

import java.security.Security;

/**
 * SerialFilter 检查器
 *
 * @author picongzhi
 */
public class SerialFilterChecker {
    private static final Log LOG = LogFactory.getLog(SerialFilterChecker.class);

    /**
     * JDK serialFilter 属性名
     */
    private static final String JDK_SERIAL_FILTER = "jdk.serialFilter";

    /**
     * 是否没有找到 serialFilter
     */
    private static final boolean SERIAL_FILTER_MISSING;

    /**
     * 是否第一次调用
     */
    private static boolean firstInvocation = true;

    static {
        Object serialFilter;

        try {
            Class<?> objectFilterConfig = Class.forName("java.io.ObjectInputFilter$Config");
            serialFilter = objectFilterConfig.getMethod("getSerialFilter")
                    .invoke(null);
        } catch (ReflectiveOperationException e) {
            serialFilter = System.getProperty(JDK_SERIAL_FILTER,
                    Security.getProperty(JDK_SERIAL_FILTER));
        }

        SERIAL_FILTER_MISSING = serialFilter == null;
    }

    private SerialFilterChecker() {
    }

    /**
     * 检查
     */
    public static void check() {
        if (firstInvocation && SERIAL_FILTER_MISSING) {
            firstInvocation = false;
            LOG.warn("As you are using functionality that deserilizes object streams, it is recommended to define the JEP-290 serial filter. "
                    + "Please refer to https://docs.oracle.com/pls/topic/lookup?ctx=javase15&id=GUID-8296D8E8-2B93-4B9A-856E-0A65AF9B8C66");
        }
    }
}

