package com.pcz.mybatis.core.reflection.property;

import com.pcz.mybatis.core.reflection.ReflectionException;

import java.util.Locale;

/**
 * 属性命名器
 *
 * @author picongzhi
 */
public final class PropertyNamer {
    private PropertyNamer() {
    }

    /**
     * 判断是不是 getter
     *
     * @param name 名称
     * @return 是不是 getter
     */
    public static boolean isGetter(String name) {
        return (name.startsWith("get") && name.length() > 3)
                || (name.startsWith("is") && name.length() > 2);
    }

    /**
     * 判断是不是 setter
     *
     * @param name 名称
     * @return 是不是 setter
     */
    public static boolean isSetter(String name) {
        return name.startsWith("set") && name.length() > 3;
    }

    /**
     * 方法名转属性
     *
     * @param name 方法名
     * @return 属性
     */
    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new ReflectionException("Error parsing property name '" + name
                    + "'. Didn't start with 'is', 'get' or 'set'");
        }

        if (name.length() == 1
                || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            // name 的长度为1 或 （name 的长度大于1 且 第二个字符不是大写），将首字母转小写
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }
}
