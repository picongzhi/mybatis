package com.pcz.mybatis.core.builder.xml;

import com.pcz.mybatis.core.builder.*;
import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.executor.ErrorContext;
import com.pcz.mybatis.core.io.Resources;
import com.pcz.mybatis.core.mapping.*;
import com.pcz.mybatis.core.parsing.XNode;
import com.pcz.mybatis.core.parsing.XPathParser;
import com.pcz.mybatis.core.reflection.MetaClass;
import com.pcz.mybatis.core.session.Configuration;
import com.pcz.mybatis.core.type.JdbcType;
import com.pcz.mybatis.core.type.TypeHandler;

import java.io.InputStream;
import java.util.*;

/**
 * 基于 XML 的 Mapper 构造器
 *
 * @author picongzhi
 */
public class XMLMapperBuilder extends BaseBuilder {
    /**
     * 路径解析器
     */
    private final XPathParser parser;

    /**
     * Mapper 构造助手
     */
    private final MapperBuilderAssistant builderAssistant;

    /**
     * SQL 片段
     */
    private final Map<String, XNode> sqlFragments;

    /**
     * 资源
     */
    private final String resource;

    public XMLMapperBuilder(InputStream inputStream,
                            Configuration configuration,
                            String resource,
                            Map<String, XNode> sqlFragments) {
        this(new XPathParser(inputStream, false, configuration.getVariables(), new XmlMapperEntityResolver()),
                configuration, resource, sqlFragments);
    }

    public XMLMapperBuilder(XPathParser parser,
                            Configuration configuration,
                            String resource,
                            Map<String, XNode> sqlFragments) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.parser = parser;
        this.sqlFragments = sqlFragments;
        this.resource = resource;
    }

    /**
     * 解析
     */
    public void parse() {
        if (!configuration.isResourceLoaded(resource)) {
            // 解析 mapper 配置
            parseConfiguration(parser.evalNode("/mapper"));

            // 加载资源
            configuration.addLoadedResource(resource);

            // 根据命名空间绑定 Mapper
            bindMapperForNamespace();
        }

        parsePendingResultMaps();

        parsePendingCacheRefs();

        parsePendingStatements();
    }

    /**
     * 解析 Mapper 配置
     *
     * @param xnode mapper 节点
     */
    private void parseConfiguration(XNode xnode) {
        try {
            // 获取 namespace
            String namespace = xnode.getStringAttribute("namespace");
            if (namespace == null || namespace.isEmpty()) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }

            // 设置 namespace
            builderAssistant.setCurrentNamespace(namespace);

            // 解析 cache-ref
            parseCacheRefElement(xnode.evalNode("cache-ref"));

            // 解析 cache
            parseCache(xnode.evalNode("cache"));

            // 解析 parameterMap
            parseParameterMap(xnode.evalNodes("/mapper/parameterMap"));

            // 解析 resultMap
            parseResultMap(xnode.evalNodes("/mapper/resultMap"));

            // 解析 sql
            parseSql(xnode.evalNodes("/mapper/sql"));

            // 构造语句
            buildStatementFromContext(xnode.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '"
                    + resource + "'. Cause: " + e, e);
        }
    }

    private void parsePendingResultMaps() {

    }

    private void parsePendingCacheRefs() {

    }

    private void parsePendingStatements() {

    }

    /**
     * 解析 cache-ref
     *
     * @param xnode cache-ref 节点
     */
    private void parseCacheRefElement(XNode xnode) {
        if (xnode == null) {
            return;
        }

        String namespace = xnode.getStringAttribute("namespace");
        configuration.addCacheRef(builderAssistant.getCurrentNamespace(), namespace);

        CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, namespace);
        try {
            cacheRefResolver.resolveCacheRef();
        } catch (IncompleteElementException e) {
            configuration.addIncompleteCacheRef(cacheRefResolver);
        }
    }

    /**
     * 解析 cache
     *
     * @param xnode cache 节点
     */
    private void parseCache(XNode xnode) {
        if (xnode == null) {
            return;
        }

        // 缓存类型，默认是 PERPETUAL，永久缓存
        String type = xnode.getStringAttribute("type", "PERPETUAL");
        Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);

        // 缓存淘汰算法，默认是 LRU，最近最少使用算法
        String eviction = xnode.getStringAttribute("eviction", "LRU");
        Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);

        // 刷新时间间隔
        Long flushInterval = xnode.getLongAttribute("flushInterval");

        // 缓存大小
        Integer size = xnode.getIntAttribute("size");

        // 是否只读
        boolean readWrite = !xnode.getBooleanAttribute("readOnly", false);

        // 是否阻塞
        boolean blocking = xnode.getBooleanAttribute("blocking", false);

        // 子节点
        Properties properties = xnode.getChildrenAsProperties();
        builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, properties);
    }

    /**
     * 解析 mapper/parameterMap
     *
     * @param xnodes mapper/parameterMap 节点
     */
    private void parseParameterMap(List<XNode> xnodes) {
        for (XNode xnode : xnodes) {
            // id
            String id = xnode.getStringAttribute("id");

            // 类型
            String type = xnode.getStringAttribute("type");
            Class<?> parameterClass = resolveClass(type);

            List<ParameterMapping> parameterMappings = new ArrayList<>();

            // 遍历所有参数
            List<XNode> parameterNodes = xnode.evalNodes("parameter");
            for (XNode parameterNode : parameterNodes) {
                // 属性名
                String property = parameterNode.getStringAttribute("property");

                // 结果集
                String resultMap = parameterNode.getStringAttribute("resultMap");
                Integer numericScale = parameterNode.getIntAttribute("numericScale");

                // mode
                String mode = parameterNode.getStringAttribute("mode");
                ParameterMode parameterMode = resolveParameterMode(mode);

                // java 类型
                String javaType = parameterNode.getStringAttribute("javaType");
                Class<?> javaTypeClass = resolveClass(javaType);

                // jdbc 类型
                String jdbcType = parameterNode.getStringAttribute("jdbcType");
                JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);

                // 类型处理器
                String typeHandler = parameterNode.getStringAttribute("typeHandler");
                Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);

                // 构造 ParameterMapping
                ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(
                        parameterClass, property, javaTypeClass, jdbcTypeEnum, resultMap, parameterMode, typeHandlerClass, numericScale);
                parameterMappings.add(parameterMapping);
            }

            builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
        }
    }

    /**
     * 解析 mapper/resultMap
     *
     * @param xnodes mapper/resultMap 节点
     */
    private void parseResultMap(List<XNode> xnodes) {
        for (XNode xnode : xnodes) {
            try {
                parseResultMapElement(xnode);
            } catch (IncompleteElementException e) {
                // 忽略
            }
        }
    }

    /**
     * 解析 resultMap 元素
     *
     * @param xnode resultMap 元素节点
     * @return 结果集
     */
    private ResultMap parseResultMapElement(XNode xnode) {
        return parseResultMapElement(xnode, Collections.emptyList(), null);
    }

    /**
     * 解析 resultMap 元素
     *
     * @param resultMapNode            resultMap 节点
     * @param additionalResultMappings 额外的 ResultMapping
     * @param enclosingType            上层元素的类型
     * @return 结果集
     */
    private ResultMap parseResultMapElement(XNode resultMapNode,
                                            List<ResultMapping> additionalResultMappings,
                                            Class<?> enclosingType) {
        ErrorContext.instance()
                .activity("processing " + resultMapNode.getValueBasedIdentifier());

        // 获取 ResultMap 类型
        String type = resultMapNode.getStringAttribute("type",
                resultMapNode.getStringAttribute("ofType",
                        resultMapNode.getStringAttribute("resultType",
                                resultMapNode.getStringAttribute("javaType"))));
        // 解析 ResultMap Class 实例
        Class<?> typeClass = resolveClass(type);
        if (typeClass == null) {
            // 如果 ResultMap Class 实例为 null，继承上层元素的类型
            typeClass = inheritEnclosingType(resultMapNode, enclosingType);
        }

        Discriminator discriminator = null;

        // ResultMapping 节点
        List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);

        // 解析 ResultMap 节点的所有子节点
        for (XNode resultMapChild : resultMapNode.getChildren()) {
            if ("constructor".equals(resultMapChild.getName())) {
                // 处理 constructor 节点
                processConstructorElement(resultMapChild, typeClass, resultMappings);
            } else if ("discriminator".equals(resultMapChild.getName())) {
                // 处理 discriminator 节点
                discriminator = processDiscriminatorElement(resultMapChild, typeClass, resultMappings);
            } else {
                List<ResultFlag> flags = new ArrayList<>();
                if ("id".equals(resultMapChild.getName())) {
                    flags.add(ResultFlag.ID);
                }
                resultMappings.add(buildResultMappingFromContext(resultMapChild, typeClass, flags));
            }
        }

        String id = resultMapNode.getStringAttribute("id",
                resultMapNode.getValueBasedIdentifier());
        String extend = resultMapNode.getStringAttribute("extends");
        Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");

        ResultMapResolver resultMapResolver = new ResultMapResolver(
                builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
        try {
            return resultMapResolver.resolve();
        } catch (IncompleteElementException e) {
            configuration.addIncompleteResultMap(resultMapResolver);
            throw e;
        }
    }

    /**
     * 继承上层节点的类型
     *
     * @param resultMapNode resultMap 节点
     * @param enclosingType 上层元素的类型
     * @return 封闭类型
     */
    private Class<?> inheritEnclosingType(XNode resultMapNode, Class<?> enclosingType) {
        if ("association".equals(resultMapNode.getName())
                && resultMapNode.getStringAttribute("resultMap") == null) {
            // association 节点 且 没有指定 resultMap
            String property = resultMapNode.getStringAttribute("property");
            if (property != null && enclosingType != null) {
                // 获取上层节点指定属性的类型，比如： <association property="name">
                MetaClass metaClass = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
                return metaClass.getSetterType(property);
            }
        } else if ("case".equals(resultMapNode.getName())
                && resultMapNode.getStringAttribute("resultMap") == null) {
            // case 节点 且 没有指定 resultMap，使用包裹 case 节点的上层节点的类型 <case value="1">
            return enclosingType;
        }

        return null;
    }

    /**
     * 处理 discriminator 元素
     *
     * @param discriminatorNode discriminator 节点
     * @param resultType        结果类型
     * @param resultMappings    结果映射
     * @return 判别器
     */
    private Discriminator processDiscriminatorElement(XNode discriminatorNode,
                                                      Class<?> resultType,
                                                      List<ResultMapping> resultMappings) {
        String column = discriminatorNode.getStringAttribute("column");

        String javaType = discriminatorNode.getStringAttribute("javaType");
        Class<?> javaTypeClass = resolveClass(javaType);

        String jdbcType = discriminatorNode.getStringAttribute("jdbcType");
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);

        String typeHandler = discriminatorNode.getStringAttribute("typeHandler");
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);

        Map<String, String> discriminatorMap = new HashMap<>();
        // 遍历 case 节点
        for (XNode caseChild : discriminatorNode.getChildren()) {
            String value = caseChild.getStringAttribute("value");
            String resultMap = caseChild.getStringAttribute("resultMap",
                    processNestedResultMappings(caseChild, resultMappings, resultType));
            discriminatorMap.put(value, resultMap);
        }

        return builderAssistant.buildDiscriminator(
                resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
    }

    /**
     * 处理 constructor 元素
     *
     * @param constructorNode constructor 节点
     * @param resultType      结果类型
     * @param resultMappings  结果映射
     */
    private void processConstructorElement(XNode constructorNode,
                                           Class<?> resultType,
                                           List<ResultMapping> resultMappings) {
        List<XNode> argNodes = constructorNode.getChildren();
        for (XNode argNode : argNodes) {
            List<ResultFlag> flags = new ArrayList<>();
            flags.add(ResultFlag.CONSTRUCTOR);
            if ("idArg".equals(argNode.getName())) {
                flags.add(ResultFlag.ID);
            }
            resultMappings.add(buildResultMappingFromContext(argNode, resultType, flags));
        }
    }

    /**
     * 构造 ResultMapping
     *
     * @param xnode      节点
     * @param resultType 结果类型
     * @param flags      结果标志
     * @return 结果映射
     */
    private ResultMapping buildResultMappingFromContext(XNode xnode, Class<?> resultType, List<ResultFlag> flags) {
        String property;
        if (flags.contains(ResultFlag.CONSTRUCTOR)) {
            property = xnode.getStringAttribute("name");
        } else {
            property = xnode.getStringAttribute("property");
        }

        String column = xnode.getStringAttribute("column");
        String nestedSelect = xnode.getStringAttribute("select");
        String nestedResultMap = xnode.getStringAttribute("resultMap",
                () -> processNestedResultMappings(xnode, Collections.emptyList(), resultType));
        String notNullColumn = xnode.getStringAttribute("notNullColumn");
        String columnPrefix = xnode.getStringAttribute("columnPrefix");
        String resultSet = xnode.getStringAttribute("resultSet");
        String foreignColumn = xnode.getStringAttribute("foreignColumn");

        boolean lazy = "lazy".equals(xnode.getStringAttribute("fetchType",
                configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));

        String javaType = xnode.getStringAttribute("javaType");
        Class<?> javaTypeClass = resolveClass(javaType);

        String typeHandler = xnode.getStringAttribute("typeHandler");
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);

        String jdbcType = xnode.getStringAttribute("jdbcType");
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);

        return builderAssistant.buildResultMapping(
                resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
    }

    /**
     * 处理嵌套的结果集
     *
     * @param xnode          节点
     * @param resultMappings 结果集
     * @param enclosingType  上层元素的类型
     * @return id
     */
    private String processNestedResultMappings(XNode xnode, List<ResultMapping> resultMappings, Class<?> enclosingType) {
        if (Arrays.asList("association", "collection", "case").contains(xnode.getName())
                && xnode.getStringAttribute("select") == null) {
            // (节点为 association 或 collection 或 case) 且 select 属性为 null
            validateCollection(xnode, enclosingType);
            ResultMap resultMap = parseResultMapElement(xnode, resultMappings, enclosingType);

            return resultMap.getId();
        }

        return null;
    }

    /**
     * 校验集合
     *
     * @param xnode         节点
     * @param enclosingType 上层元素的类型
     */
    private void validateCollection(XNode xnode, Class<?> enclosingType) {
        if ("collection".equals(xnode.getName())
                && xnode.getStringAttribute("resultMap") == null
                && xnode.getStringAttribute("javaType") == null) {
            // 节点为 collection 且 resultMap 为 null 且 javaType 为 null
            MetaClass metaClass = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
            String property = xnode.getStringAttribute("property");
            if (!metaClass.hasSetter(property)) {
                throw new BuilderException("Ambiguous collection type for property '" + property
                        + "'. You must specify 'javaType' for 'resultMap.'");
            }
        }
    }

    /**
     * 解析 /mapper/sql
     *
     * @param xnodes /mapper/sql 节点
     */
    private void parseSql(List<XNode> xnodes) {
        if (configuration.getDatabaseId() != null) {
            parseSql(xnodes, configuration.getDatabaseId());
        }
        parseSql(xnodes, null);
    }

    /**
     * 解析 /mapper/sql
     *
     * @param xnodes             /mapper/sql 节点
     * @param requiredDatabaseId 需要的数据库 id
     */
    private void parseSql(List<XNode> xnodes, String requiredDatabaseId) {
        for (XNode xnode : xnodes) {
            String databaseId = xnode.getStringAttribute("databaseId");

            String id = xnode.getStringAttribute("id");
            id = builderAssistant.applyCurrentNamespace(id, false);

            if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
                sqlFragments.put(id, xnode);
            }
        }
    }

    /**
     * 判断数据库 id 是否和当前匹配
     *
     * @param id                 id
     * @param databaseId         数据库 id
     * @param requiredDatabaseId 需要的数据库 id
     * @return 是否匹配
     */
    private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
        if (requiredDatabaseId != null) {
            return requiredDatabaseId.equals(databaseId);
        }

        if (databaseId != null) {
            return false;
        }

        if (!this.sqlFragments.containsKey(id)) {
            return true;
        }

        XNode xnode = this.sqlFragments.get(id);
        return xnode.getStringAttribute("databaseId") == null;
    }

    /**
     * 构造语句
     * select|insert|update|delete
     *
     * @param xnodes select|insert|update|delete 节点
     */
    private void buildStatementFromContext(List<XNode> xnodes) {
        if (configuration.getDatabaseId() != null) {
            buildStatementFromContext(xnodes, configuration.getDatabaseId());
        }
        buildStatementFromContext(xnodes, null);
    }

    /**
     * 构造语句
     *
     * @param xnodes             select|insert|update|delete 节点
     * @param requiredDatabaseId 需要的数据库 id
     */
    private void buildStatementFromContext(List<XNode> xnodes, String requiredDatabaseId) {
        for (XNode xnode : xnodes) {
            final XMLStatementBuilder statementBuilder = new XMLStatementBuilder(
                    configuration, builderAssistant, xnode, requiredDatabaseId);
            try {
                statementBuilder.parseStatementNode();
            } catch (IncompleteElementException e) {
                configuration.addIncompleteStatement(statementBuilder);
            }
        }
    }

    /**
     * 根据命名空间绑定 Mapper
     */
    private void bindMapperForNamespace() {
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace == null) {
            return;
        }

        Class<?> boundType = null;
        try {
            boundType = Resources.classForName(namespace);
        } catch (ClassNotFoundException e) {
            // 忽略
        }

        if (boundType != null && !configuration.hasMapper(boundType)) {
            configuration.addLoadedResource("namespace:" + namespace);
            configuration.addMapper(boundType);
        }
    }
}
