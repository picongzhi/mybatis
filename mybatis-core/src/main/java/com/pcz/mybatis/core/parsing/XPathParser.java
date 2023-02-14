package com.pcz.mybatis.core.parsing;

import com.pcz.mybatis.core.builder.BuilderException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * X Path 解析器
 *
 * @author picongzhi
 */
public class XPathParser {
    /**
     * 文档
     */
    private final Document document;

    /**
     * 是否进行校验
     */
    private boolean validation;

    /**
     * 实例解析器
     */
    private EntityResolver entityResolver;

    /**
     * 变量
     */
    private Properties variables;

    /**
     * XPath
     */
    protected XPath xpath;

    public XPathParser(Reader reader, boolean validation, Properties variables, EntityResolver entityResolver) {
        this.variables = variables;
        this.validation = validation;
        this.entityResolver = entityResolver;

        XPathFactory xpathFactory = XPathFactory.newInstance();
        this.xpath = xpathFactory.newXPath();

        this.document = createDocument(new InputSource(reader));
    }

    public XPathParser(InputStream inputStream, boolean validation, Properties variables, EntityResolver entityResolver) {
        this.variables = variables;
        this.validation = validation;
        this.entityResolver = entityResolver;

        XPathFactory xpathFactory = XPathFactory.newInstance();
        this.xpath = xpathFactory.newXPath();

        this.document = createDocument(new InputSource(inputStream));
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    /**
     * 计算 String 值
     *
     * @param expression 表达式
     * @return String 值
     */
    public String evalString(String expression) {
        return evalString(document, expression);
    }

    /**
     * 计算 String 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return String 值
     */
    public String evalString(Object root, String expression) {
        String result = (String) evaluate(expression, root, XPathConstants.STRING);
        result = PropertyParser.parse(result, variables);

        return result;
    }

    /**
     * 计算 Boolean 值
     *
     * @param expression 表达式
     * @return Boolean 值
     */
    public Boolean evalBoolean(String expression) {
        return evalBoolean(document, expression);
    }

    /**
     * 计算 Boolean 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return Boolean 值
     */
    public Boolean evalBoolean(Object root, String expression) {
        return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
    }

    /**
     * 计算 Short 值
     *
     * @param expression 表达式
     * @return Short 值
     */
    public Short evalShort(String expression) {
        return evalShort(document, expression);
    }

    /**
     * 计算 Short 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return Short 值
     */
    public Short evalShort(Object root, String expression) {
        return Short.valueOf(evalString(root, expression));
    }


    /**
     * 计算 Integer 值
     *
     * @param expression 表达式
     * @return Integer 值
     */
    public Integer evalInteger(String expression) {
        return evalInteger(document, expression);
    }

    /**
     * 计算 Integer 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return Integer 值
     */
    public Integer evalInteger(Object root, String expression) {
        return Integer.valueOf(evalString(root, expression));
    }

    /**
     * 计算 Long 值
     *
     * @param expression 表达式
     * @return Long 值
     */
    public Long evalLong(String expression) {
        return evalLong(document, expression);
    }

    /**
     * 计算 Long 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return Long 值
     */
    public Long evalLong(Object root, String expression) {
        return Long.valueOf(evalString(root, expression));
    }

    /**
     * 计算 Float 值
     *
     * @param expression 表达式
     * @return Float 值
     */
    public Float evalFloat(String expression) {
        return evalFloat(document, expression);
    }

    /**
     * 计算 Float 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return Float 值
     */
    public Float evalFloat(Object root, String expression) {
        return Float.valueOf(evalString(root, expression));
    }

    /**
     * 计算 Double 值
     *
     * @param expression 表达式
     * @return Double 值
     */
    public Double evalDouble(String expression) {
        return evalDouble(document, expression);
    }

    /**
     * 计算 Double 值
     *
     * @param root       根节点
     * @param expression 表达式
     * @return Double 值
     */
    public Double evalDouble(Object root, String expression) {
        return (Double) evaluate(expression, root, XPathConstants.NUMBER);
    }

    /**
     * 计算节点
     *
     * @param expression 表达式
     * @return XNode
     */
    public XNode evalNode(String expression) {
        return evalNode(document, expression);
    }

    /**
     * 计算节点
     *
     * @param root       根节点
     * @param expression 表达式
     * @return XNode
     */
    public XNode evalNode(Object root, String expression) {
        Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
        if (node == null) {
            return null;
        }

        return new XNode(this, node, variables);
    }

    /**
     * 结算节点列表
     *
     * @param root       根节点
     * @param expression 表达式
     * @return List<XNode>
     */
    public List<XNode> evalNodes(Object root, String expression) {
        List<XNode> xnodes = new ArrayList<>();

        NodeList nodeList = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            xnodes.add(new XNode(this, nodeList.item(i), variables));
        }

        return xnodes;
    }

    /**
     * 计算
     *
     * @param expression 表达式
     * @param root       根节点
     * @param returnType 返回类型
     * @return 节点值
     */
    private Object evaluate(String expression, Object root, QName returnType) {
        try {
            return xpath.evaluate(expression, root, returnType);
        } catch (Exception e) {
            throw new BuilderException("Error evaluating XPath. Cause: " + e, e);
        }
    }

    /**
     * 创建文档
     *
     * @param inputSource 输入源
     * @return 文档
     */
    private Document createDocument(InputSource inputSource) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setValidating(this.validation);
            documentBuilderFactory.setNamespaceAware(false);
            documentBuilderFactory.setIgnoringComments(true);
            documentBuilderFactory.setIgnoringElementContentWhitespace(false);
            documentBuilderFactory.setCoalescing(false);
            documentBuilderFactory.setExpandEntityReferences(true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(this.entityResolver);
            documentBuilder.setErrorHandler(new InnerErrorHandler());

            return documentBuilder.parse(inputSource);
        } catch (Exception e) {
            throw new BuilderException("Error creating document instance, cause: " + e, e);
        }
    }

    /**
     * 内部的错误处理器
     */
    private static class InnerErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            // do nothing
        }
    }
}
