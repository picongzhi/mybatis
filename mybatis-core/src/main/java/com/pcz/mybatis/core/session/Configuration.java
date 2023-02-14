package com.pcz.mybatis.core.session;

import com.pcz.mybatis.core.binding.MapperRegistry;
import com.pcz.mybatis.core.builder.CacheRefResolver;
import com.pcz.mybatis.core.builder.ResultMapResolver;
import com.pcz.mybatis.core.builder.xml.XMLStatementBuilder;
import com.pcz.mybatis.core.datasource.unpooled.UnpooledDataSourceFactory;
import com.pcz.mybatis.core.executor.loader.ProxyFactory;
import com.pcz.mybatis.core.executor.loader.cglib.CglibProxyFactory;
import com.pcz.mybatis.core.executor.loader.javassist.JavassistProxyFactory;
import com.pcz.mybatis.core.io.VFS;
import com.pcz.mybatis.core.logging.Log;
import com.pcz.mybatis.core.logging.LogFactory;
import com.pcz.mybatis.core.logging.slf4j.Slf4jImpl;
import com.pcz.mybatis.core.mapping.Environment;
import com.pcz.mybatis.core.mapping.MappedStatement;
import com.pcz.mybatis.core.mapping.ResultSetType;
import com.pcz.mybatis.core.mapping.VendorDatabaseIdProvider;
import com.pcz.mybatis.core.parsing.XNode;
import com.pcz.mybatis.core.plugins.Interceptor;
import com.pcz.mybatis.core.plugins.InterceptorChain;
import com.pcz.mybatis.core.reflection.DefaultReflectorFactory;
import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.ReflectorFactory;
import com.pcz.mybatis.core.reflection.factory.DefaultObjectFactory;
import com.pcz.mybatis.core.reflection.factory.ObjectFactory;
import com.pcz.mybatis.core.reflection.wrapper.DefaultObjectWrapperFactory;
import com.pcz.mybatis.core.reflection.wrapper.ObjectWrapperFactory;
import com.pcz.mybatis.core.scripting.LanguageDriver;
import com.pcz.mybatis.core.scripting.LanguageDriverRegistry;
import com.pcz.mybatis.core.scripting.defaults.RawLanguageDriver;
import com.pcz.mybatis.core.scripting.xmltags.XMLLanguageDriver;
import com.pcz.mybatis.core.transaction.jdbc.JdbcTransactionFactory;
import com.pcz.mybatis.core.type.JdbcType;
import com.pcz.mybatis.core.type.TypeAliasRegistry;
import com.pcz.mybatis.core.type.TypeHandler;
import com.pcz.mybatis.core.type.TypeHandlerRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * 配置
 *
 * @author picongzhi
 */
public class Configuration {
    /**
     * 环境
     */
    protected Environment environment;

    /**
     * 是否开启缓存
     */
    protected boolean cacheEnabled = true;

    /**
     * 是否使用列标签
     */
    protected boolean useColumnLabel = true;

    /**
     * 是否使用生成的键
     */
    protected boolean useGeneratedKeys;

    /**
     * 是否开启下划线转驼峰
     */
    protected boolean mapUnderscoreToCamelCase;

    /**
     * 是否开启安全的行绑定
     */
    protected boolean safeRowBoundsEnabled;

    /**
     * 是否对 null 值调用 setter
     */
    protected boolean callSettersOnNulls;

    /**
     * 是否使用真实的参数名
     */
    protected boolean useActualParamName = true;

    /**
     * 是否在空行时返回实例
     */
    protected boolean returnInstanceForEmptyRow;

    /**
     * 是否缩小 SQL 中的空白
     */
    protected boolean shrinkWhitespacesInSql;

    /**
     * 是否基于构造器参数名自动映射
     */
    protected boolean argNameBasedConstructorAutoMapping;

    /**
     * 是否允许 for 循环遍历中为 null
     */
    protected boolean nullableOnForEach;

    /**
     * 日志前缀
     */
    protected String logPrefix;

    /**
     * 日志实现类
     */
    protected Class<? extends Log> logImpl;

    /**
     * VFS 实现类
     */
    protected Class<? extends VFS> vfsImpl;

    /**
     * 默认的 SqlProvider Class 实例
     */
    protected Class<?> defaultSqlProviderType;

    /**
     * 默认的执行器类型
     */
    protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;

    /**
     * 默认的语句超时时间
     */
    protected Integer defaultStatementTimeout;

    /**
     * 默认获取数
     */
    protected Integer defaultFetchSize;

    /**
     * 默认的结果集类型
     */
    protected ResultSetType defaultResultSetType;

    /**
     * 自动映射行为
     */
    protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;

    /**
     * 自动映射未知字段的行为
     */
    protected AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;

    /**
     * 本地缓存作用域
     */
    protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;

    /**
     * 空值默认的 Jdbc 类型
     */
    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;

    /**
     * 懒加载触发方法
     */
    protected Set<String> lazyLoadTriggerMethods = new HashSet<>(
            Arrays.asList("equals", "clone", "hashCode", "toString"));

    /**
     * 是否启用懒加载
     */
    protected boolean lazyLoadingEnabled = false;

    /**
     * 是否启用更激进的懒加载
     */
    protected boolean aggressiveLazyLoading = false;

    /**
     * 是否启用多个结果集
     */
    protected boolean multipleResultSetsEnabled = true;

    /**
     * 是否开启安全的结果处理器
     */
    protected boolean safeResultHandlerEnabled = true;

    /**
     * 变量
     */
    protected Properties variables = new Properties();

    /**
     * 对象工厂
     */
    protected ObjectFactory objectFactory = new DefaultObjectFactory();

    /**
     * 包装对象工厂
     */
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    /**
     * 反射器工厂
     */
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    /**
     * 拦截器链
     */
    protected final InterceptorChain interceptorChain = new InterceptorChain();

    /**
     * 代理工厂
     */
    protected ProxyFactory proxyFactory = new JavassistProxyFactory();

    /**
     * 数据库运营商 id
     */
    protected String databaseId;

    /**
     * 配置工厂 Class 实例
     */
    protected Class<?> configurationFactory;

    /**
     * 类型别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    /**
     * 语言驱动注册器
     */
    protected final LanguageDriverRegistry languageDriverRegistry = new LanguageDriverRegistry();

    /**
     * 类型处理器注册器
     */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry(this);

    /**
     * Mapper 注册器
     */
    private final MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 映射的语句
     */
    private final Map<String, MappedStatement> mappedStatements =
            new StrictMap<MappedStatement>("Mapped Statements collection")
                    .conflictMessageProducer((savedValue, targetValue) ->
                            ". please check " + savedValue.getResource() + " and " + targetValue.getResource());

    /**
     * SQL 片段
     */
    protected final Map<String, XNode> sqlFragments =
            new StrictMap<>("XML fragments parsed from previous mappers");

    /**
     * 加载的资源
     */
    protected final Set<String> loadedResources = new HashSet<>();

    /**
     * 未完成的 CacheRef
     */
    protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<>();

    /**
     * 未完成的 ResultMap
     */
    protected final Collection<ResultMapResolver> incompleteResultMaps = new LinkedList<>();

    /**
     * 未完成的 Statement
     */
    protected final Collection<XMLStatementBuilder> incompleteStatements = new LinkedList<>();

    /**
     * namespace -> referenceNamespace
     */
    protected final Map<String, String> cacheRefMap = new HashMap<>();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);

        typeAliasRegistry.registerAlias("DB_VENDOR", VendorDatabaseIdProvider.class);

        typeAliasRegistry.registerAlias("SLF4J", Slf4jImpl.class);

        typeAliasRegistry.registerAlias("CGLIB", CglibProxyFactory.class);

        languageDriverRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
        languageDriverRegistry.register(RawLanguageDriver.class);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }

    public void setUseColumnLabel(boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isSafeRowBoundsEnabled() {
        return safeRowBoundsEnabled;
    }

    public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
        this.safeRowBoundsEnabled = safeRowBoundsEnabled;
    }

    public boolean isCallSetterOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public boolean isUseActualParamName() {
        return useActualParamName;
    }

    public void setUseActualParamName(boolean useActualParamName) {
        this.useActualParamName = useActualParamName;
    }

    public boolean isReturnInstanceForEmptyRow() {
        return returnInstanceForEmptyRow;
    }

    public void setReturnInstanceForEmptyRow(boolean returnInstanceForEmptyRow) {
        this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
    }

    public boolean isShrinkWhitespacesInSql() {
        return shrinkWhitespacesInSql;
    }

    public void setShrinkWhitespacesInSql(boolean shrinkWhitespacesInSql) {
        this.shrinkWhitespacesInSql = shrinkWhitespacesInSql;
    }

    public boolean isArgNameBasedConstructorAutoMapping() {
        return argNameBasedConstructorAutoMapping;
    }

    public void setArgNameBasedConstructorAutoMapping(boolean argNameBasedConstructorAutoMapping) {
        this.argNameBasedConstructorAutoMapping = argNameBasedConstructorAutoMapping;
    }

    public boolean isNullableOnForEach() {
        return nullableOnForEach;
    }

    public void setNullableOnForEach(boolean nullableOnForEach) {
        this.nullableOnForEach = nullableOnForEach;
    }

    public String getLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public Class<? extends Log> getLogImpl() {
        return logImpl;
    }

    public void setLogImpl(Class<? extends Log> logImpl) {
        if (logImpl != null) {
            this.logImpl = logImpl;
            LogFactory.useCustomLogging(this.logImpl);
        }
    }

    public Class<? extends VFS> getVfsImpl() {
        return vfsImpl;
    }

    public void setVfsImpl(Class<? extends VFS> vfsImpl) {
        if (vfsImpl != null) {
            this.vfsImpl = vfsImpl;
            VFS.addImplClass(this.vfsImpl);
        }
    }

    public Class<?> getDefaultSqlProviderType() {
        return defaultSqlProviderType;
    }

    public void setDefaultSqlProviderType(Class<?> defaultSqlProviderType) {
        this.defaultSqlProviderType = defaultSqlProviderType;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public AutoMappingBehavior getAutoMappingBehavior() {
        return autoMappingBehavior;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public void setDefaultFetchSize(Integer defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public ResultSetType getDefaultResultSetType() {
        return defaultResultSetType;
    }

    public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
        this.defaultResultSetType = defaultResultSetType;
    }

    public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
        this.autoMappingBehavior = autoMappingBehavior;
    }

    public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
        return autoMappingUnknownColumnBehavior;
    }

    public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
        this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
    }

    public LocalCacheScope getLocalCacheScope() {
        return localCacheScope;
    }

    public void setLocalCacheScope(LocalCacheScope localCacheScope) {
        this.localCacheScope = localCacheScope;
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
        this.jdbcTypeForNull = jdbcTypeForNull;
    }

    public Set<String> getLazyLoadTriggerMethods() {
        return lazyLoadTriggerMethods;
    }

    public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
        this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
    }

    public boolean isLazyLoadingEnabled() {
        return lazyLoadingEnabled;
    }

    public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
        this.lazyLoadingEnabled = lazyLoadingEnabled;
    }

    public boolean isAggressiveLazyLoading() {
        return aggressiveLazyLoading;
    }

    public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
        this.aggressiveLazyLoading = aggressiveLazyLoading;
    }


    public boolean isMultipleResultSetsEnabled() {
        return multipleResultSetsEnabled;
    }

    public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
        this.multipleResultSetsEnabled = multipleResultSetsEnabled;
    }

    public boolean isSafeResultHandlerEnabled() {
        return safeResultHandlerEnabled;
    }

    public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
        this.safeResultHandlerEnabled = safeResultHandlerEnabled;
    }

    public Properties getVariables() {
        return variables;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        this.objectWrapperFactory = objectWrapperFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public void setReflectorFactory(ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }

    public LanguageDriverRegistry getLanguageDriverRegistry() {
        return languageDriverRegistry;
    }

    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageDriverRegistry.getDefaultDriver();
    }

    public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
        if (driver == null) {
            driver = XMLLanguageDriver.class;
        }

        getLanguageDriverRegistry().setDefaultDriverClass(driver);
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        if (proxyFactory == null) {
            proxyFactory = new JavassistProxyFactory();
        }
        this.proxyFactory = proxyFactory;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public Class<?> getConfigurationFactory() {
        return configurationFactory;
    }

    public void setConfigurationFactory(Class<?> configurationFactory) {
        this.configurationFactory = configurationFactory;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> cls) {
        mapperRegistry.addMapper(cls);
    }

    public <T> T getMapper(Class<T> cls, SqlSession sqlSession) {
        return mapperRegistry.getMapper(cls, sqlSession);
    }

    public boolean hasMapper(Class<?> cls) {
        return mapperRegistry.hasMapper(cls);
    }

    public MappedStatement getMappedStatement(String id) {
        return this.getMappedStatement(id, true);
    }

    public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }

        return mappedStatements.get(id);
    }

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public boolean hasMappedStatement(String id) {
        return hasMappedStatement(id, true);
    }

    public boolean hasMappedStatement(String id, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }

        return mappedStatements.containsKey(id);
    }

    public Collection<String> getMappedStatementNames() {
        buildAllStatements();
        return mappedStatements.keySet();
    }

    public Collection<MappedStatement> getMappedStatements() {
        buildAllStatements();
        return mappedStatements.values();
    }

    /**
     * 构建所有语句
     */
    protected void buildAllStatements() {
        // TODO: 实现构建
    }

    public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandler) {
        if (typeHandler != null) {
            getTypeHandlerRegistry().setDefaultEnumTypeHandler(typeHandler);
        }
    }

    public Map<String, XNode> getSqlFragments() {
        return sqlFragments;
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public Collection<CacheRefResolver> getIncompleteCacheRefs() {
        return incompleteCacheRefs;
    }

    public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
        incompleteCacheRefs.add(incompleteCacheRef);
    }

    public Collection<ResultMapResolver> getIncompleteResultMaps() {
        return incompleteResultMaps;
    }

    public void addIncompleteResultMap(ResultMapResolver incompleteResult) {
        incompleteResultMaps.add(incompleteResult);
    }

    public Collection<XMLStatementBuilder> getIncompleteStatements() {
        return incompleteStatements;
    }

    public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
        incompleteStatements.add(incompleteStatement);
    }

    public void addCacheRef(String namespace, String referenceNamespace) {
        cacheRefMap.put(namespace, referenceNamespace);
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    /**
     * 严格的 Map
     *
     * @param <V> 值泛型
     */
    protected static class StrictMap<V> extends ConcurrentHashMap<String, V> {
        /**
         * 名称
         */
        private final String name;

        /**
         * 冲突消息生成器
         */
        private BiFunction<V, V, String> conflictMessageProducer;

        public StrictMap(String name) {
            super();
            this.name = name;
        }

        /**
         * 设置冲突消息生成器
         *
         * @param conflictMessageProducer 冲突消息生成器
         * @return 当前实例
         */
        public StrictMap<V> conflictMessageProducer(BiFunction<V, V, String> conflictMessageProducer) {
            this.conflictMessageProducer = conflictMessageProducer;
            return this;
        }

        @Override
        public V put(String key, V value) {
            if (containsKey(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key
                        + (conflictMessageProducer == null ?
                        "" :
                        conflictMessageProducer.apply(super.get(key), value)));
            }

            if (key.contains(".")) {
                final String shortKey = getShortName(key);
                if (super.get(shortKey) == null) {
                    super.put(shortKey, value);
                } else {
                    super.put(shortKey, (V) new Ambiguity(shortKey));
                }
            }

            return super.put(key, value);
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }

            return super.get(key) != null;
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }

            if (value instanceof Ambiguity) {
                throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name
                        + " (try using the full name including the namespace, or rename one of the entries)");
            }

            return value;
        }

        /**
         * 获取 key 缩写
         *
         * @param key 输入 key
         * @return 缩写 key
         */
        private String getShortName(String key) {
            final String[] keyParts = key.split("\\.");
            return keyParts[keyParts.length - 1];
        }

        /**
         * 有歧义的值
         */
        protected static class Ambiguity {
            /**
             * 主题
             */
            private final String subject;

            public Ambiguity(String subject) {
                this.subject = subject;
            }

            public String getSubject() {
                return subject;
            }
        }
    }
}
