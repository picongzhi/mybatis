package com.pcz.mybatis.core.reflection.wrapper;

import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * 包装对象
 *
 * @author picongzhi
 */
public interface ObjectWrapper {
    /**
     * 获取属性值
     *
     * @param propertyTokenizer 属性分词器
     * @return 对象
     */
    Object get(PropertyTokenizer propertyTokenizer);

    /**
     * 设置属性值
     *
     * @param propertyTokenizer 属性分词器
     * @param value             值
     */
    void set(PropertyTokenizer propertyTokenizer, Object value);

    /**
     * 获取属性
     *
     * @param name                属性名
     * @param useCamelCaseMapping 是否使用驼峰映射
     * @return 属性值
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 获取 Getter 名
     *
     * @return Getter 名
     */
    String[] getGetterNames();

    /**
     * 获取 Setter 名
     *
     * @return Setter 名
     */
    String[] getSetterNames();

    /**
     * 获取 Getter 类型
     *
     * @param name Getter 名称
     * @return Getter 类型
     */
    Class<?> getGetterType(String name);

    /**
     * 获取 Setter 类型
     *
     * @param name Setter 名称
     * @return Setter 类型
     */
    Class<?> getSetterType(String name);

    /**
     * 判断是否有 Getter
     *
     * @param name Getter 名称
     * @return 是否有 Getter
     */
    boolean hasGetter(String name);

    /**
     * 判断是否有 Setter
     *
     * @param name Setter 名称
     * @return 是否有 Setter
     */
    boolean hasSetter(String name);

    /**
     * 实例化属性值
     *
     * @param name              属性名
     * @param propertyTokenizer 属性分词器
     * @param objectFactory     对象工厂
     * @return 元信息对象
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer propertyTokenizer, ObjectFactory objectFactory);

    /**
     * 判断是否是集合
     *
     * @return 是否是集合
     */
    boolean isCollection();

    /**
     * 添加元素
     *
     * @param element 元素
     */
    void add(Object element);

    /**
     * 添加多个元素
     *
     * @param element 元素
     * @param <E>     元素类型泛型
     */
    <E> void addAll(List<E> element);
}
