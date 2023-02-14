package com.pcz.mybatis.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数名工具类
 *
 * @author picongzhi
 */
public class ParamNameUtil {
    private ParamNameUtil() {
        super();
    }

    /**
     * 获取方法参数名
     *
     * @param method 方法
     * @return 参数名
     */
    public static List<String> getParamNames(Method method) {
        return getParameterNames(method);
    }

    /**
     * 获取构造器参数名
     *
     * @param constructor 构造器
     * @return 参数名
     */
    public static List<String> getParamNames(Constructor<?> constructor) {
        return getParameterNames(constructor);
    }

    /**
     * 获取参数名
     *
     * @param executable 可执行对象
     * @return 参数名
     */
    private static List<String> getParameterNames(Executable executable) {
        return Arrays.stream(executable.getParameters())
                .map(Parameter::getName)
                .collect(Collectors.toList());
    }
}
