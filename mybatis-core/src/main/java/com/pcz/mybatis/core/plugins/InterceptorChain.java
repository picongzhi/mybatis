package com.pcz.mybatis.core.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 拦截器链
 *
 * @author picongzhi
 */
public class InterceptorChain {
    /**
     * 拦截器
     */
    private final List<Interceptor> interceptors = new ArrayList<>();

    /**
     * 集成所有插件
     *
     * @param target 目标对象
     * @return 集成后的对象
     */
    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }

        return target;
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     */
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * 获取所有拦截器
     *
     * @return 所有拦截器
     */
    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
}
