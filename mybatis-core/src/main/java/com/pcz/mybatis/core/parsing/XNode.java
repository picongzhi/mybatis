package com.pcz.mybatis.core.parsing;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * 节点
 *
 * @author picongzhi
 */
public class XNode {
    /**
     * 文档节点
     */
    private final Node node;

    /**
     * 节点名
     */
    private final String name;

    /**
     * 节点 body
     */
    private final String body;

    /**
     * 属性
     */
    private final Properties attributes;

    /**
     * 变量
     */
    private final Properties variables;

    /**
     * 解析器
     */
    private final XPathParser xpathParser;

    public XNode(XPathParser xpathParser, Node node, Properties variables) {
        this.xpathParser = xpathParser;
        this.node = node;
        this.name = node.getNodeName();
        this.variables = variables;
        this.attributes = parseAttribute(node);
        this.body = parseBody(node);
    }

    /**
     * 获取下一个节点
     *
     * @param node 节点
     * @return 节点
     */
    public XNode nextXNode(Node node) {
        return new XNode(xpathParser, node, variables);
    }

    /**
     * 获取父节点
     *
     * @return 父节点
     */
    public XNode getParent() {
        Node parent = node.getParentNode();
        if (!(parent instanceof Element)) {
            return null;
        } else {
            return new XNode(xpathParser, parent, variables);
        }
    }

    /**
     * 获取节点路径
     *
     * @return 节点路径
     */
    public String getPath() {
        StringBuilder builder = new StringBuilder();

        Node current = node;
        while (current instanceof Element) {
            if (current != node) {
                builder.insert(0, "/");
            }

            builder.insert(0, current.getNodeName());
            current = current.getParentNode();
        }

        return builder.toString();
    }

    /**
     * 获取基于值的标识符
     *
     * @return 基于值的标识符
     */
    public String getValueBasedIdentifier() {
        StringBuilder builder = new StringBuilder();

        XNode current = this;
        while (current != null) {
            if (current != this) {
                builder.insert(0, "_");
            }

            String value = current.getStringAttribute("id",
                    current.getStringAttribute("value",
                            current.getStringAttribute("property", (String) null)));
            if (value != null) {
                value = value.replace(',', '_');
                builder.insert(0, "]");
                builder.insert(0, value);
                builder.insert(0, "[");
            }
            builder.insert(0, current.getName());

            current = current.getParent();
        }

        return builder.toString();
    }

    /**
     * 计算 String
     *
     * @param expression 表达式
     * @return String
     */
    public String evalString(String expression) {
        return xpathParser.evalString(node, expression);
    }

    /**
     * 计算 Boolean
     *
     * @param expression 表达式
     * @return Boolean
     */
    public Boolean evalBoolean(String expression) {
        return xpathParser.evalBoolean(node, expression);
    }

    /**
     * 计算 Double
     *
     * @param expression 表达式
     * @return Double
     */
    public Double evalDouble(String expression) {
        return xpathParser.evalDouble(node, expression);
    }

    /**
     * 计算节点
     *
     * @param expression 表达式
     * @return 节点
     */
    public XNode evalNode(String expression) {
        return xpathParser.evalNode(node, expression);
    }

    /**
     * 计算节点列表
     *
     * @param expression 表达式
     * @return 节点列表
     */
    public List<XNode> evalNodes(String expression) {
        return xpathParser.evalNodes(node, expression);
    }

    /**
     * 获取节点
     *
     * @return 节点
     */
    public Node getNode() {
        return node;
    }

    /**
     * 获取节点名
     *
     * @return 节点名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取 String 类型的 body
     *
     * @return body
     */
    public String getStringBody() {
        return getStringBody(null);
    }

    /**
     * 获取 String 类型的 body
     *
     * @param defaultValue 默认值
     * @return body
     */
    public String getStringBody(String defaultValue) {
        return body == null
                ? defaultValue
                : body;
    }

    /**
     * 获取 Boolean 类型的 body
     *
     * @return body
     */
    public Boolean getBooleanBody() {
        return getBooleanBody(null);
    }

    /**
     * 获取 Boolean 类型的 body
     *
     * @param defaultValue 默认值
     * @return body
     */
    public Boolean getBooleanBody(Boolean defaultValue) {
        return body == null
                ? defaultValue
                : Boolean.valueOf(body);
    }

    /**
     * 获取 Integer 类型的 body
     *
     * @return body
     */
    public Integer getIntBody() {
        return getIntBody(null);
    }

    /**
     * 获取 Integer 类型的 body
     *
     * @param defaultValue 默认值
     * @return body
     */
    public Integer getIntBody(Integer defaultValue) {
        return body == null
                ? defaultValue
                : Integer.valueOf(body);
    }

    /**
     * 获取 Long 类型的 body
     *
     * @return body
     */
    public Long getLongBody() {
        return getLongBody(null);
    }

    /**
     * 获取 Long 类型的 body
     *
     * @param defaultValue 默认值
     * @return body
     */
    public Long getLongBody(Long defaultValue) {
        return body == null
                ? defaultValue
                : Long.valueOf(body);
    }

    /**
     * 获取 Double 类型的 body
     *
     * @return body
     */
    public Double getDoubleBody() {
        return getDoubleBody(null);
    }

    /**
     * 获取 Double 类型的 body
     *
     * @param defaultValue 默认值
     * @return body
     */
    public Double getDoubleBody(Double defaultValue) {
        return body == null
                ? defaultValue
                : Double.valueOf(body);
    }

    /**
     * 获取 Float 类型的 body
     *
     * @return body
     */
    public Float getFloatBody() {
        return getFloatBody(null);
    }

    /**
     * 获取 Float 类型的 body
     *
     * @param defaultValue 默认值
     * @return body
     */
    public Float getFloatBody(Float defaultValue) {
        return body == null
                ? defaultValue
                : Float.valueOf(defaultValue);
    }

    /**
     * 获取枚举属性
     *
     * @param enumType 枚举类型
     * @param name     属性名
     * @param <T>      枚举泛型
     * @return 枚举属性
     */
    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name) {
        return getEnumAttribute(enumType, name, null);
    }

    /**
     * 获取枚举属性
     *
     * @param enumType     枚举类型
     * @param name         属性名
     * @param defaultValue 默认值
     * @param <T>          枚举泛型
     * @return 枚举属性
     */
    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name, T defaultValue) {
        String value = getStringAttribute(name);
        return value == null
                ? defaultValue
                : Enum.valueOf(enumType, value);
    }

    /**
     * 获取 String 类型的属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public String getStringAttribute(String name) {
        return getStringAttribute(name, (String) null);
    }

    /**
     * 获取 String 类型的属性
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public String getStringAttribute(String name, String defaultValue) {
        String value = attributes.getProperty(name);
        return value == null
                ? defaultValue
                : value;
    }

    /**
     * 获取 String 类型的属性
     *
     * @param name            属性名
     * @param defaultSupplier 默认值 Supplier
     * @return 属性值
     */
    public String getStringAttribute(String name, Supplier<String> defaultSupplier) {
        return getStringAttribute(name, defaultSupplier.get());
    }

    /**
     * 获取 Boolean 类型的属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Boolean getBooleanAttribute(String name) {
        return getBooleanAttribute(name, null);
    }

    /**
     * 获取 Boolean 类型的属性
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Boolean getBooleanAttribute(String name, Boolean defaultValue) {
        String value = attributes.getProperty(name);
        return value == null
                ? defaultValue
                : Boolean.valueOf(value);
    }

    /**
     * 获取 Integer 类型的属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Integer getIntAttribute(String name) {
        return getIntAttribute(name, null);
    }

    /**
     * 获取 Integer 类型的属性
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Integer getIntAttribute(String name, Integer defaultValue) {
        String value = attributes.getProperty(name);
        return value == null
                ? defaultValue
                : Integer.valueOf(value);
    }

    /**
     * 获取 Long 类型的属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Long getLongAttribute(String name) {
        return getLongAttribute(name, null);
    }

    /**
     * 获取 Long 类型的属性
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Long getLongAttribute(String name, Long defaultValue) {
        String value = attributes.getProperty(name);
        return value == null
                ? defaultValue
                : Long.valueOf(value);
    }

    /**
     * 获取 Double 类型的属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Double getDoubleAttribute(String name) {
        return getDoubleAttribute(name, null);
    }

    /**
     * 获取 Double 类型的属性
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Double getDoubleAttribute(String name, Double defaultValue) {
        String value = attributes.getProperty(name);
        return value == null
                ? defaultValue
                : Double.valueOf(value);
    }

    /**
     * 获取 Float 类型的属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Float getFloatAttribute(String name) {
        return getFloatAttribute(name, null);
    }

    /**
     * 获取 Float 类型的属性
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Float getFloatAttribute(String name, Float defaultValue) {
        String value = attributes.getProperty(name);
        return value == null
                ? defaultValue
                : Float.valueOf(value);
    }

    /**
     * 获取子节点
     *
     * @return 子节点
     */
    public List<XNode> getChildren() {
        List<XNode> children = new ArrayList<>();

        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(new XNode(xpathParser, node, variables));
                }
            }
        }

        return children;
    }

    /**
     * 获取子节点，以 Properties 的格式
     *
     * @return 子节点属性
     */
    public Properties getChildrenAsProperties() {
        Properties properties = new Properties();

        for (XNode child : getChildren()) {
            String name = child.getStringAttribute("name");
            String value = child.getStringAttribute("value");
            if (name != null && value != null) {
                properties.setProperty(name, value);
            }
        }

        return properties;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(builder, 0);

        return builder.toString();
    }

    /**
     * 转 String
     *
     * @param builder 构造器
     * @param level   层级
     */
    private void toString(StringBuilder builder, int level) {
        builder.append("<");
        builder.append(name);

        for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
            builder.append(" ");
            builder.append(entry.getKey());
            builder.append("=\"");
            builder.append(entry.getValue());
            builder.append("\"");
        }

        List<XNode> children = getChildren();
        if (!children.isEmpty()) {
            builder.append(">\n");
            for (XNode child : children) {
                indent(builder, level + 1);
                child.toString(builder, level + 1);
            }

            indent(builder, level);
            builder.append("</");
            builder.append(name);
            builder.append(">");
        } else if (body != null) {
            builder.append(">");
            builder.append(body);
            builder.append("</");
            builder.append(name);
            builder.append(">");
        } else {
            builder.append("/>");
            indent(builder, level);
        }

        builder.append("\n");
    }

    /**
     * 缩进
     *
     * @param builder 构造器
     * @param level   层级
     */
    private void indent(StringBuilder builder, int level) {
        for (int i = 0; i < level; i++) {
            builder.append("    ");
        }
    }

    /**
     * 解析属性
     *
     * @param node 节点
     * @return 属性
     */
    private Properties parseAttribute(Node node) {
        Properties properties = new Properties();

        NamedNodeMap attributeNodes = node.getAttributes();
        if (attributeNodes == null) {
            return properties;
        }

        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Node attributeNode = attributeNodes.item(i);
            String value = PropertyParser.parse(attributeNode.getNodeValue(), variables);
            properties.put(attributeNode.getNodeName(), value);
        }

        return properties;
    }

    /**
     * 解析节点 body
     *
     * @param node 节点
     * @return 节点 body
     */
    private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }

        return data;
    }

    /**
     * 获取节点 body 数据
     *
     * @param node 节点
     * @return 节点 body 数据
     */
    private String getBodyData(Node node) {
        if (node.getNodeType() == Node.CDATA_SECTION_NODE
                || node.getNodeType() == Node.TEXT_NODE) {
            String data = ((CharacterData) node).getData();
            data = PropertyParser.parse(data, variables);

            return data;
        }

        return null;
    }
}
