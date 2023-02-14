package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.invoker.GetFieldInvoker;
import com.pcz.mybatis.core.reflection.invoker.Invoker;
import com.pcz.mybatis.core.reflection.invoker.MethodInvoker;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Class 元信息
 *
 * @author picongzhi
 */
public class MetaClass {
    /**
     * 反射器工厂
     */
    private final ReflectorFactory reflectorFactory;

    /**
     * 反射器
     */
    private final Reflector reflector;

    private MetaClass(Class<?> cls, ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(cls);
    }

    /**
     * 获取 Class 元信息
     *
     * @param cls              Class 实例
     * @param reflectorFactory 反射器工厂
     * @return Class 元信息
     */
    public static MetaClass forClass(Class<?> cls, ReflectorFactory reflectorFactory) {
        return new MetaClass(cls, reflectorFactory);
    }

    /**
     * 获取属性的 Class 元信息
     *
     * @param name 属性名
     * @return 属性的 Class 元信息
     */
    public MetaClass metaClassForProperty(String name) {
        Class<?> propertyType = reflector.getGetterType(name);
        return MetaClass.forClass(propertyType, reflectorFactory);
    }

    /**
     * 获取属性
     *
     * @param name 属性名
     * @return 属性
     */
    public String findProperty(String name) {
        StringBuilder property = buildProperty(name, new StringBuilder());
        return property.length() > 0 ? property.toString() : null;
    }

    /**
     * 获取属性
     *
     * @param name                属性名
     * @param useCamelCaseMapping 是否使用驼峰转换
     * @return 属性
     */
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            name = name.replace("_", "");
        }
        return findProperty(name);
    }

    /**
     * 获取所有的 getter 名
     *
     * @return 所有的 getter 名
     */
    public String[] getGetterNames() {
        return reflector.getReadablePropertyNames();
    }

    /**
     * 获取所有的 setter 名
     *
     * @return 所有的 setter 名
     */
    public String[] getSetterNames() {
        return reflector.getWritablePropertyNames();
    }

    /**
     * 获取 getter 类型
     *
     * @param name 属性名
     * @return getter 类型
     */
    public Class<?> getGetterType(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaClass propertyMetaClass = metaClassForProperty(propertyTokenizer);
            return propertyMetaClass.getGetterType(propertyTokenizer.getChildren());
        }

        return getGetterType(propertyTokenizer);
    }

    /**
     * 获取 setter 类型
     *
     * @param name 属性名
     * @return setter 类型
     */
    public Class<?> getSetterType(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            MetaClass propertyMetaClass = metaClassForProperty(propertyTokenizer.getName());
            return propertyMetaClass.getSetterType(propertyTokenizer.getChildren());
        }

        return reflector.getSetterType(propertyTokenizer.getName());
    }

    /**
     * 获取 getter 类型
     *
     * @param propertyTokenizer 属性分词器
     * @return getter 类型
     */
    private Class<?> getGetterType(PropertyTokenizer propertyTokenizer) {
        Class<?> type = reflector.getGetterType(propertyTokenizer.getName());
        if (propertyTokenizer.getIndex() != null
                && Collection.class.isAssignableFrom(type)) {
            Type returnType = getGenericGetterType(propertyTokenizer.getName());
            if (returnType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    returnType = actualTypeArguments[0];
                    if (returnType instanceof Class) {
                        type = (Class<?>) returnType;
                    } else if (returnType instanceof ParameterizedType) {
                        type = (Class<?>) ((ParameterizedType) returnType).getRawType();
                    }
                }
            }
        }

        return type;
    }

    /**
     * 获取 getter 类型
     *
     * @param propertyName 属性名
     * @return getter 类型
     */
    private Type getGenericGetterType(String propertyName) {
        try {
            Invoker invoker = reflector.getGetInvoker(propertyName);
            if (invoker instanceof MethodInvoker) {
                Field declaredField = MethodInvoker.class.getDeclaredField("method");
                declaredField.setAccessible(true);

                Method method = (Method) declaredField.get(invoker);
                return TypeParameterResolver.resolveReturnType(method, reflector.getType());
            } else if (invoker instanceof GetFieldInvoker) {
                Field declaredField = GetFieldInvoker.class.getDeclaredField("field");
                declaredField.setAccessible(true);

                Field field = (Field) declaredField.get(invoker);
                return TypeParameterResolver.resolveFieldType(field, reflector.getType());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }

        return null;
    }

    /**
     * 判断是否有 getter
     *
     * @param name 属性名
     * @return 是否有 getter
     */
    public boolean hasGetter(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            if (reflector.hasGetter(propertyTokenizer.getName())) {
                MetaClass propertyMetaClass = metaClassForProperty(propertyTokenizer);
                return propertyMetaClass.hasGetter(propertyTokenizer.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasGetter(propertyTokenizer.getName());
        }
    }

    /**
     * 判断是否有 setter
     *
     * @param name 属性名
     * @return 是否有 setter
     */
    public boolean hasSetter(String name) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            if (reflector.hasSetter(propertyTokenizer.getName())) {
                MetaClass propertyMetaClass = metaClassForProperty(propertyTokenizer.getName());
                return propertyMetaClass.hasSetter(propertyTokenizer.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasSetter(propertyTokenizer.getName());
        }
    }

    /**
     * 获取 get invoker
     *
     * @param name 属性名
     * @return get invoker
     */
    public Invoker getGetInvoker(String name) {
        return reflector.getGetInvoker(name);
    }

    /**
     * 获取 set invoker
     *
     * @param name 属性名
     * @return set invoker
     */
    public Invoker getSetInvoker(String name) {
        return reflector.getSetInvoker(name);
    }

    /**
     * 判断是否有默认构造器
     *
     * @return 是否有默认构造器
     */
    public boolean hasDefaultConstructor() {
        return reflector.hasDefaultConstructor();
    }

    /**
     * 构建属性
     *
     * @param name          属性名
     * @param stringBuilder 构建器
     * @return 构建结果
     */
    private StringBuilder buildProperty(String name, StringBuilder stringBuilder) {
        PropertyTokenizer propertyTokenizer = new PropertyTokenizer(name);
        if (propertyTokenizer.hasNext()) {
            String propertyName = reflector.findPropertyName(propertyTokenizer.getName());
            if (propertyName != null) {
                stringBuilder.append(propertyName);
                stringBuilder.append(".");

                MetaClass propertyMetaClass = metaClassForProperty(propertyName);
                propertyMetaClass.buildProperty(propertyTokenizer.getChildren(), stringBuilder);
            }
        } else {
            String propertyName = reflector.findPropertyName(name);
            if (propertyName != null) {
                stringBuilder.append(propertyName);
            }
        }

        return stringBuilder;
    }

    /**
     * 获取属性 Class 元信息
     *
     * @param propertyTokenizer 属性分词器
     * @return 属性 Class 元信息
     */
    private MetaClass metaClassForProperty(PropertyTokenizer propertyTokenizer) {
        Class<?> propertyType = getGetterType(propertyTokenizer);
        return MetaClass.forClass(propertyType, reflectorFactory);
    }
}
