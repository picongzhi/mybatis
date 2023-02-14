package com.pcz.mybatis.core.builder.plugin;

import com.pcz.mybatis.core.plugins.Interceptor;
import com.pcz.mybatis.core.plugins.Invocation;

public class CustomPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }
}
