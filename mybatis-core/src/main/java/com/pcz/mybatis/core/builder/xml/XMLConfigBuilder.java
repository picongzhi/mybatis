package com.pcz.mybatis.core.builder.xml;

import com.pcz.mybatis.core.builder.BaseBuilder;
import com.pcz.mybatis.core.builder.BuilderException;
import com.pcz.mybatis.core.datasource.DataSourceFactory;
import com.pcz.mybatis.core.executor.ErrorContext;
import com.pcz.mybatis.core.executor.loader.ProxyFactory;
import com.pcz.mybatis.core.io.Resources;
import com.pcz.mybatis.core.io.VFS;
import com.pcz.mybatis.core.logging.Log;
import com.pcz.mybatis.core.mapping.DatabaseIdProvider;
import com.pcz.mybatis.core.mapping.Environment;
import com.pcz.mybatis.core.parsing.XNode;
import com.pcz.mybatis.core.parsing.XPathParser;
import com.pcz.mybatis.core.plugins.Interceptor;
import com.pcz.mybatis.core.reflection.DefaultReflectorFactory;
import com.pcz.mybatis.core.reflection.MetaClass;
import com.pcz.mybatis.core.reflection.ReflectorFactory;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.wrapper.ObjectWrapperFactory;
import com.pcz.mybatis.core.session.*;
import com.pcz.mybatis.core.transaction.TransactionFactory;
import com.pcz.mybatis.core.type.JdbcType;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * Xml 配置构造器
 *
 * @author picongzhi
 */
public class XMLConfigBuilder extends BaseBuilder {
    /**
     * 解析器
     */
    private final XPathParser parser;

    /**
     * 是否已解析
     */
    private boolean parsed;

    /**
     * 环境
     */
    private String environment;

    /**
     * 本地反射器工厂
     */
    private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    public XMLConfigBuilder(Reader reader) {
        super(new Configuration());
        parser = new XPathParser(reader, false, null, null);
        parsed = false;
    }

    /**
     * 解析获取配置
     *
     * @return 配置
     */
    public Configuration parse() {
        if (parsed) {
            throw new BuilderException("Each XmlConfigBuilder can only be used once.");
        }

        // 标记为已解析
        parsed = true;

        // 获取 root node
        XNode root = parser.evalNode("/configuration");

        // 解析 configuration
        parseConfiguration(root);

        // 返回 configuration
        return configuration;
    }

    /**
     * 解析配置
     *
     * @param root 根节点
     */
    private void parseConfiguration(XNode root) {
        try {
            // 解析 properties
            parseProperties(root.evalNode("properties"));

            // 获取 settings
            Properties settings = getSettingsAsProperties(root.evalNode("settings"));
            // 加载自定义 Vfs
            loadCustomVfs(settings);
            // 加载自定义 Log 实现
            loadCustomLogImpl(settings);

            // 解析 typeAlias
            parseTypeAlias(root.evalNode("typeAliases"));

            // 解析 plugins
            parsePlugins(root.evalNode("plugins"));

            // 解析 objectFactory
            parseObjectFactory(root.evalNode("objectFactory"));

            // 解析 objectWrapperFactory
            parseObjectWrapperFactory(root.evalNode("objectWrapperFactory"));

            // 解析 reflectorFactory
            parseReflectorFactory(root.evalNode("reflectorFactory"));

            // 解析 settings
            parseSettings(settings);

            // 解析 environments
            parseEnvironments(root.evalNode("environments"));

            // 解析 databaseIdProvider
            parseDatabaseIdProvider(root.evalNode("databaseIdProvider"));

            // 解析 typeHandlers
            parseTypeHandlers(root.evalNode("typeHandlers"));

            // 解析 mappers
            parseMappers(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    /**
     * 解析属性
     *
     * @param xnode properties 节点
     * @throws Exception 解析异常
     */
    private void parseProperties(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        String resource = xnode.getStringAttribute("resource");
        String url = xnode.getStringAttribute("url");
        if (resource != null && url != null) {
            throw new BuilderException("The properties element cannot specify " +
                    "both a URL and a resource based property file reference. Please specify one or the other");
        }

        Properties properties = xnode.getChildrenAsProperties();
        if (resource != null) {
            properties.putAll(Resources.getResourceAsProperties(resource));
        } else if (url != null) {
            properties.putAll(Resources.getUrlAsProperties(url));
        }

        Properties variables = configuration.getVariables();
        if (variables != null) {
            properties.putAll(variables);
        }

        parser.setVariables(properties);
        configuration.setVariables(properties);
    }

    /**
     * 获取 settings
     *
     * @param xnode settings 节点
     * @return settings
     */
    private Properties getSettingsAsProperties(XNode xnode) {
        if (xnode == null) {
            return new Properties();
        }

        MetaClass metaClass = MetaClass.forClass(Configuration.class, localReflectorFactory);

        // 获取属性并校验
        Properties properties = xnode.getChildrenAsProperties();
        for (Object key : properties.keySet()) {
            if (!metaClass.hasSetter(String.valueOf(key))) {
                throw new BuilderException("The setting " + key
                        + " is not known. Make sure you spelled it correctly (case sensitive)");
            }
        }

        return properties;
    }

    /**
     * 加载自定义 VFS
     *
     * @param settings settings
     * @throws ClassNotFoundException 没有找到 Class 异常
     */
    @SuppressWarnings("unchecked")
    private void loadCustomVfs(Properties settings) throws ClassNotFoundException {
        String value = settings.getProperty("vfsImpl");
        if (value != null) {
            String[] classes = value.split(",");
            for (String cls : classes) {
                if (!cls.isEmpty()) {
                    Class<? extends VFS> vfsImpl = (Class<? extends VFS>) Resources.classForName(cls);
                    configuration.setVfsImpl(vfsImpl);
                }
            }
        }
    }

    /**
     * 加载自定义 Log 实现
     *
     * @param settings settings
     */
    private void loadCustomLogImpl(Properties settings) {
        Class<? extends Log> logImpl = resolveClass(settings.getProperty("logImpl"));
        configuration.setLogImpl(logImpl);
    }

    /**
     * 解析 typeAliases 节点
     *
     * @param xnode typeAliases 节点
     */
    private void parseTypeAlias(XNode xnode) {
        if (xnode == null) {
            return;
        }

        for (XNode child : xnode.getChildren()) {
            if ("package".equals(child.getName())) {
                // package 节点
                String typeAliasPackage = child.getStringAttribute("name");
                configuration.getTypeAliasRegistry()
                        .registerAliases(typeAliasPackage);
            } else {
                // typeAlias 节点
                String alias = child.getStringAttribute("alias");
                String type = child.getStringAttribute("type");

                try {
                    Class<?> cls = Resources.classForName(type);
                    if (alias == null) {
                        typeAliasRegistry.registerAlias(cls);
                    } else {
                        typeAliasRegistry.registerAlias(alias, cls);
                    }
                } catch (ClassNotFoundException e) {
                    throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
                }
            }
        }
    }

    /**
     * 解析 plugins 节点
     *
     * @param xnode plugins 节点
     * @throws Exception 解析异常
     */
    private void parsePlugins(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        for (XNode child : xnode.getChildren()) {
            // 实例化
            String interceptor = child.getStringAttribute("interceptor");
            Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor)
                    .getDeclaredConstructor()
                    .newInstance();

            // 设置属性
            Properties properties = child.getChildrenAsProperties();
            interceptorInstance.setProperties(properties);

            configuration.addInterceptor(interceptorInstance);
        }
    }

    /**
     * 解析 objectFactory
     *
     * @param xnode objectFactory 节点
     * @throws Exception 解析异常
     */
    private void parseObjectFactory(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        // 实例化
        String type = xnode.getStringAttribute("type");
        ObjectFactory objectFactory = (ObjectFactory) resolveClass(type)
                .getDeclaredConstructor()
                .newInstance();

        // 设置属性
        Properties properties = xnode.getChildrenAsProperties();
        objectFactory.setProperties(properties);

        configuration.setObjectFactory(objectFactory);
    }

    /**
     * 解析 objectWrapperFactory
     *
     * @param xnode objectWrapperFactory 节点
     * @throws Exception 解析异常
     */
    private void parseObjectWrapperFactory(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        String type = xnode.getStringAttribute("type");
        ObjectWrapperFactory objectWrapperFactory = (ObjectWrapperFactory) resolveClass(type)
                .getDeclaredConstructor()
                .newInstance();

        configuration.setObjectWrapperFactory(objectWrapperFactory);
    }

    /**
     * 解析 reflectorFactory
     *
     * @param xnode reflectorFactory 节点
     * @throws Exception 解析异常
     */
    private void parseReflectorFactory(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        String type = xnode.getStringAttribute("type");
        ReflectorFactory reflectorFactory = (ReflectorFactory) resolveClass(type)
                .getDeclaredConstructor()
                .newInstance();

        configuration.setReflectorFactory(reflectorFactory);
    }

    /**
     * 解析 settings
     *
     * @param settings setting 属性
     */
    private void parseSettings(Properties settings) {
        // 设置自动映射行为
        configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(
                settings.getProperty("autoMappingBehavior", "PARTIAL")));

        // 设置未知字段的自动映射行为
        configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(
                settings.getProperty("autoMappingUnknownColumnBehavior", "NONE")));

        // 设置是否启用缓存
        configuration.setCacheEnabled(
                booleanValueOf(settings.getProperty("cacheEnabled"), true));

        // 设置代理工厂
        configuration.setProxyFactory(
                (ProxyFactory) createInstance(settings.getProperty("proxyFactory")));

        // 设置是否启用懒加载
        configuration.setLazyLoadingEnabled(
                booleanValueOf(settings.getProperty("lazyLoadingEnabled"), false));

        // 和 lazyLoadingEnabled 配合使用，如果 lazyLoadingEnabled 为 true，aggressiveLazyLoading 为 true 则加载所有懒加载对象，为 false 则按需
        configuration.setAggressiveLazyLoading(
                booleanValueOf(settings.getProperty("aggressiveLazyLoading"), false));

        // 设置是否允许一条 SQL 返回多个结果集
        configuration.setMultipleResultSetsEnabled(
                booleanValueOf(settings.getProperty("multipleResultSetsEnabled"), true));

        // 设置是否使用列索引替代名称
        configuration.setUseColumnLabel(
                booleanValueOf(settings.getProperty("useColumnLabel"), true));

        // 设置是否允许 JDBC 生成主键
        configuration.setUseGeneratedKeys(
                booleanValueOf(settings.getProperty("useGeneratedKeys"), false));

        // 设置默认的执行器类型
        configuration.setDefaultExecutorType(ExecutorType.valueOf(
                settings.getProperty("defaultExecutorType", "SIMPLE")));

        // 设置默认的语句超时时间
        configuration.setDefaultStatementTimeout(
                integerValueOf(settings.getProperty("defaultStatementTimeout"), null));

        // 设置默认的抓取数量
        configuration.setDefaultFetchSize(
                integerValueOf(settings.getProperty("defaultFetchSize"), null));

        // 设置默认的结果集类型
        configuration.setDefaultResultSetType(
                resolveResultSetType(settings.getProperty("defaultResultSetType")));

        // 设置是否开启下划线转驼峰
        configuration.setMapUnderscoreToCamelCase(
                booleanValueOf(settings.getProperty("mapUnderscoreToCamelCase"), false));

        // 设置是否开允许在嵌套语句中使用分页（RowBounds）
        configuration.setSafeRowBoundsEnabled(
                booleanValueOf(settings.getProperty("safeRowBoundsEnabled"), false));

        // 设置本地缓存的作用域
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(
                settings.getProperty("localCacheScope", "SESSION")));

        // 设置 JDBC 类型全名
        configuration.setJdbcTypeForNull(JdbcType.valueOf(
                settings.getProperty("jdbcTypeForNull", "OTHER")));

        // 设置触发懒加载的方法
        configuration.setLazyLoadTriggerMethods(
                stringSetValueOf(settings.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));

        // 是否允许在嵌套语句中使用结果处理器，false 表示允许
        configuration.setSafeResultHandlerEnabled(
                booleanValueOf(settings.getProperty("safeResultHandlerEnabled"), true));

        // 设置动态 SQL 生成的默认脚本语言
        configuration.setDefaultScriptingLanguage(
                resolveClass(settings.getProperty("defaultScriptingLanguage")));

        // 设置默认的枚举类型处理器
        configuration.setDefaultEnumTypeHandler(
                resolveClass(settings.getProperty("defaultEnumTypeHandler")));

        // 设置是否对 null 值调用 setter
        configuration.setCallSettersOnNulls(
                booleanValueOf(settings.getProperty("callSettersOnNulls"), false));

        // 设置是否使用真实的参数名
        configuration.setUseActualParamName(
                booleanValueOf(settings.getProperty("useActualParamName"), true));

        // 设置是否在空行时返回实例
        configuration.setReturnInstanceForEmptyRow(
                booleanValueOf(settings.getProperty("returnInstanceForEmptyRow"), false));

        // 设置日志前缀
        configuration.setLogPrefix(settings.getProperty("logPrefix"));

        // 设置配置工厂
        configuration.setConfigurationFactory(
                resolveClass(settings.getProperty("configurationFactory")));

        // 是否缩小 SQL 中的空白
        configuration.setShrinkWhitespacesInSql(
                booleanValueOf(settings.getProperty("shrinkWhitespaceInSql"), false));

        // 设置基于构造器参数名自动映射
        configuration.setArgNameBasedConstructorAutoMapping(
                booleanValueOf(settings.getProperty("argNameBasedConstructorAutoMapping"), false));

        // 设置默认的 SqlProvider 类型
        configuration.setDefaultSqlProviderType(
                resolveClass(settings.getProperty("defaultSqlProviderType")));

        // 设置是否在 for 循环遍历时允许 null
        configuration.setNullableOnForEach(
                booleanValueOf(settings.getProperty("nullableOnForEach"), false));
    }

    /**
     * 解析 environments 节点
     *
     * @param xnode environments 节点
     * @throws Exception 解析异常
     */
    private void parseEnvironments(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        if (environment == null) {
            // 获取默认的环境
            environment = xnode.getStringAttribute("default");
        }

        // 遍历所有环境配置
        for (XNode child : xnode.getChildren()) {
            String id = child.getStringAttribute("id");
            if (isSpecifiedEnvironment(id)) {
                // 解析 transactionManager
                TransactionFactory transactionFactory =
                        parseTransactionFactory(child.evalNode("transactionManager"));

                // 解析 Datasource
                DataSourceFactory dataSourceFactory =
                        parseDataSourceFactory(child.evalNode("datasource"));
                DataSource dataSource = dataSourceFactory.getDataSource();

                // 构造 Environment 并设置到 Configuration
                Environment environment = new Environment.Builder(id)
                        .transactionFactory(transactionFactory)
                        .datasource(dataSource)
                        .build();
                configuration.setEnvironment(environment);

                break;
            }
        }
    }

    /**
     * 解析 transactionManager 节点
     *
     * @param xnode transactionManager 节点
     * @return 事务工厂
     * @throws Exception 异常
     */
    private TransactionFactory parseTransactionFactory(XNode xnode) throws Exception {
        if (xnode == null) {
            return null;
        }

        // 获取事务管理器类型
        String type = xnode.getStringAttribute("type");

        // 实例化事务管理器
        TransactionFactory transactionFactory = (TransactionFactory) resolveClass(type)
                .getDeclaredConstructor()
                .newInstance();

        // 获取属性并设置到事务管理器
        Properties properties = xnode.getChildrenAsProperties();
        transactionFactory.setProperties(properties);

        return transactionFactory;
    }

    /**
     * 解析数据源工厂
     *
     * @param xnode datasource 节点
     * @return 数据源工厂
     * @throws Exception 异常
     */
    private DataSourceFactory parseDataSourceFactory(XNode xnode) throws Exception {
        if (xnode == null) {
            return null;
        }

        // 获取数据源工厂类型
        String type = xnode.getStringAttribute("type");

        // 实例化数据源工厂
        DataSourceFactory dataSourceFactory = (DataSourceFactory) resolveClass(type)
                .getDeclaredConstructor()
                .newInstance();

        // 获取属性并设置到数据源工厂
        Properties properties = xnode.getChildrenAsProperties();
        dataSourceFactory.setProperty(properties);

        return dataSourceFactory;
    }

    /**
     * 解析 databaseIdProvider
     *
     * @param xnode databaseIdProvider 节点
     * @throws Exception 解析异常
     */
    private void parseDatabaseIdProvider(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        // 供应商类型
        String type = xnode.getStringAttribute("type");
        if ("VENDOR".equals(type)) {
            type = "DB_VENDOR";
        }

        // 实例化
        DatabaseIdProvider databaseIdProvider = (DatabaseIdProvider) resolveClass(type)
                .getDeclaredConstructor()
                .newInstance();

        // 设置属性
        Properties properties = xnode.getChildrenAsProperties();
        databaseIdProvider.setProperties(properties);

        Environment environment = configuration.getEnvironment();
        if (environment != null) {
            String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
            configuration.setDatabaseId(databaseId);
        }
    }

    /**
     * 解析 typeHandlers 节点
     *
     * @param xnode typeHandlers 节点
     */
    private void parseTypeHandlers(XNode xnode) {
        if (xnode == null) {
            return;
        }

        for (XNode child : xnode.getChildren()) {
            if ("package".equals(child.getName())) {
                // 扫描 package 下所有的 handler
                String typeHandlerPackage = child.getStringAttribute("name");
                typeHandlerRegistry.register(typeHandlerPackage);
            } else {
                // 单个 handler
                String javaTypeName = child.getStringAttribute("javaType");
                String jdbcTypeName = child.getStringAttribute("jdbcType");
                String typeHandlerName = child.getStringAttribute("handler");

                Class<?> typeHandlerClass = resolveClass(typeHandlerName);
                Class<?> javaTypeClass = resolveClass(javaTypeName);
                if (javaTypeClass != null) {
                    JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
                    if (jdbcType == null) {
                        typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
                    } else {
                        typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
                    }
                } else {
                    typeHandlerRegistry.register(typeHandlerClass);
                }
            }
        }
    }

    /**
     * 解析 mappers 节点
     *
     * @param xnode mappers 节点
     * @throws Exception 解析异常
     */
    private void parseMappers(XNode xnode) throws Exception {
        if (xnode == null) {
            return;
        }

        for (XNode child : xnode.getChildren()) {
            if ("package".equals(child.getName())) {
                // 扫描 package 下的所有 Mapper
                String mapperPackageName = child.getStringAttribute("name");
                configuration.addMappers(mapperPackageName);
            } else {
                // 单的 Mapper
                String resource = child.getStringAttribute("resource");
                String url = child.getStringAttribute("url");
                String mapperClass = child.getStringAttribute("class");

                if (resource != null && url == null && mapperClass == null) {
                    // 资源
                    ErrorContext.instance().resource(resource);
                    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
                        XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(
                                inputStream, configuration, resource, configuration.getSqlFragments());
                        mapperBuilder.parse();
                    }
                } else if (resource == null && url != null && mapperClass == null) {
                    // url
                    ErrorContext.instance().resource(url);
                    try (InputStream inputStream = Resources.getUrlAsStream(url)) {
                        XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(
                                inputStream, configuration, url, configuration.getSqlFragments());
                        mapperBuilder.parse();
                    }
                } else if (resource == null && url == null && mapperClass != null) {
                    // Class
                    Class<?> mapperInterface = Resources.classForName(mapperClass);
                    configuration.addMapper(mapperInterface);
                } else {
                    throw new BuilderException("A mapper element may only specify a url, " +
                            "resource or class, but not more than one.");
                }
            }
        }
    }

    /**
     * 判断指定的环境是否和当前环境匹配匹配
     *
     * @param id 指定的环境
     * @return 是否匹配
     */
    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new BuilderException("No environment specified.");
        }

        if (id == null) {
            throw new BuilderException("Environment requires an id attribute");
        }

        return environment.equals(id);
    }
}
