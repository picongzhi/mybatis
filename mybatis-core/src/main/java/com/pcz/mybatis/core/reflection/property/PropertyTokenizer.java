package com.pcz.mybatis.core.reflection.property;

import java.util.Iterator;

/**
 * 属性分词器
 * name 或 name.children 或 name[indexName].children
 *
 * @author picongzhi
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
    /**
     * 属性名
     */
    private String name;

    /**
     * 索引属性名
     */
    private final String indexedName;

    /**
     * 索引
     */
    private String index;

    /**
     * 子属性
     */
    private final String children;

    public PropertyTokenizer(String propertyName) {
        int delim = propertyName.indexOf('.');
        if (delim > -1) {
            name = propertyName.substring(0, delim);
            children = propertyName.substring(delim + 1);
        } else {
            name = propertyName;
            children = null;
        }

        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1) {
            index = name.substring(delim + 1, name.length() - 1);
            name = name.substring(0, delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "Remove is not supported, as it has no meaning in the context of properties.");
    }
}
