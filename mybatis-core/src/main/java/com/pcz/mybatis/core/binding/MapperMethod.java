package com.pcz.mybatis.core.binding;

import com.pcz.mybatis.core.annotations.Flush;
import com.pcz.mybatis.core.annotations.MapKey;
import com.pcz.mybatis.core.cursor.Cursor;
import com.pcz.mybatis.core.mapping.MappedStatement;
import com.pcz.mybatis.core.mapping.SqlCommandType;
import com.pcz.mybatis.core.mapping.StatementType;
import com.pcz.mybatis.core.reflection.MetaObject;
import com.pcz.mybatis.core.reflection.ParamNameResolver;
import com.pcz.mybatis.core.reflection.TypeParameterResolver;
import com.pcz.mybatis.core.session.Configuration;
import com.pcz.mybatis.core.session.ResultHandler;
import com.pcz.mybatis.core.session.RowBounds;
import com.pcz.mybatis.core.session.SqlSession;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Mapper 方法
 *
 * @author picongzhi
 */
public class MapperMethod {
    /**
     * SQL 命令
     */
    private final SqlCommand sqlCommand;

    /**
     * 方法签名
     */
    private final MethodSignature methodSignature;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.sqlCommand = new SqlCommand(configuration, mapperInterface, method);
        this.methodSignature = new MethodSignature(configuration, mapperInterface, method);
    }

    /**
     * 执行
     *
     * @param sqlSession Sql 会话
     * @param args       参数
     * @return 执行结果
     */
    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (sqlCommand.getType()) {
            case INSERT: {
                // insert
                Object param = methodSignature.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.insert(sqlCommand.getName(), param));
                break;
            }
            case UPDATE: {
                // update
                Object param = methodSignature.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.update(sqlCommand.getName(), param));
                break;
            }
            case DELETE: {
                // delete
                Object param = methodSignature.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.delete(sqlCommand.getName(), param));
                break;
            }
            case SELECT:
                // select
                if (methodSignature.returnsVoid() && methodSignature.hasResultHandler()) {
                    // 返回空
                    executeWithResultHandler(sqlSession, args);
                    result = null;
                } else if (methodSignature.returnsMany()) {
                    // 返回多个
                    result = executeForMany(sqlSession, args);
                } else if (methodSignature.returnsMap()) {
                    // 返回 map
                    result = executeForMap(sqlSession, args);
                } else if (methodSignature.returnsCursor()) {
                    // 返回游标
                    result = executeForCursor(sqlSession, args);
                } else {
                    Object param = methodSignature.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(sqlCommand.getName(), param);
                    if (methodSignature.returnsOptional()
                            && (result == null || !methodSignature.getReturnType().equals(result.getClass()))) {
                        result = Optional.ofNullable(result);
                    }
                }
                break;
            case FLUSH:
                // flush
                result = sqlSession.flushStatements();
                break;
            default:
                throw new BindingException("Unknown execution method for: " + sqlCommand.getName());
        }

        if (result == null && methodSignature.getReturnType().isPrimitive() && !methodSignature.returnsVoid()) {
            throw new BindingException("Mapper method '" + sqlCommand.getName()
                    + "' attempted to return null from a method with a primitive return type ("
                    + methodSignature.getReturnType() + ").");
        }

        return result;
    }

    /**
     * 获取影响行数结果
     *
     * @param rowCount 行数
     * @return 结果
     */
    private Object rowCountResult(int rowCount) {
        final Object result;

        if (methodSignature.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(methodSignature.getReturnType())
                || Integer.TYPE.equals(methodSignature.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(methodSignature.getReturnType())
                || Long.TYPE.equals(methodSignature.getReturnType())) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(methodSignature.getReturnType())
                || Boolean.TYPE.equals(methodSignature.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new BindingException("Mapper method '" + sqlCommand.getName()
                    + "' has an unsupported return type: " + methodSignature.getReturnType());
        }

        return result;
    }

    /**
     * 使用结果处理器执行
     *
     * @param sqlSession SQL 会话
     * @param args       参数
     */
    private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
        MappedStatement mappedStatement = sqlSession.getConfiguration()
                .getMappedStatement(sqlCommand.getName());
        if (!StatementType.CALLABLE.equals(mappedStatement.getStatementType())
                && void.class.equals(mappedStatement.getResultMaps().get(0).getType())) {
            throw new BindingException("method " + sqlCommand.getName()
                    + " needs either a @ResultMap annotation, a @ResultType annotation,"
                    + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
        }

        Object param = methodSignature.convertArgsToSqlCommandParam(args);
        if (methodSignature.hasRowBounds()) {
            RowBounds rowBounds = methodSignature.extractRowBounds(args);
            sqlSession.select(sqlCommand.getName(),
                    param, rowBounds, methodSignature.extractResultHandler(args));
        } else {
            sqlSession.select(sqlCommand.getName(),
                    param, methodSignature.extractResultHandler(args));
        }
    }

    /**
     * 执行并获取多个结果
     *
     * @param sqlSession SQL 会话
     * @param args       参数
     * @param <E>        泛型
     * @return 结果
     */
    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        List<E> result;

        Object param = methodSignature.convertArgsToSqlCommandParam(args);
        if (methodSignature.hasRowBounds()) {
            RowBounds rowBounds = methodSignature.extractRowBounds(args);
            result = sqlSession.selectList(sqlCommand.getName(), param, rowBounds);
        } else {
            result = sqlSession.selectList(sqlCommand.getName(), param);
        }

        if (!methodSignature.getReturnType().isAssignableFrom(result.getClass())) {
            if (methodSignature.getReturnType().isArray()) {
                // 转 Array
                return convertToArray(result);
            } else {
                // 转 指定类型的 Collection
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            }
        }

        return result;
    }

    /**
     * 将 List 转 Array
     *
     * @param list List
     * @param <E>  元素泛型
     * @return Array
     */
    @SuppressWarnings("unchecked")
    private <E> Object convertToArray(List<E> list) {
        Class<?> arrayComponentType = methodSignature.getReturnType().getComponentType();
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (arrayComponentType.isPrimitive()) {
            // Array 元素是原生类型
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }

            return array;
        } else {
            // Array 元素不是原生类型
            return list.toArray((E[]) array);
        }
    }

    /**
     * 将 List 转 声明的集合类型
     *
     * @param configuration 配置
     * @param list          List
     * @param <E>           元素泛型
     * @return 集合
     */
    private <E> Object convertToDeclaredCollection(Configuration configuration, List<E> list) {
        Object collection = configuration.getObjectFactory()
                .create(methodSignature.getReturnType());
        MetaObject metaObject = configuration.newMetaObject(collection);
        metaObject.addAll(list);

        return collection;
    }

    /**
     * 执行并获取 Map
     *
     * @param sqlSession SQL 会话
     * @param args       参数
     * @param <K>        key 泛型
     * @param <V>        value 泛型
     * @return Map
     */
    private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
        Map<K, V> result;

        Object param = methodSignature.convertArgsToSqlCommandParam(args);
        if (methodSignature.hasRowBounds()) {
            RowBounds rowBounds = methodSignature.extractRowBounds(args);
            result = sqlSession.selectMap(sqlCommand.getName(), param, methodSignature.getMapKey(), rowBounds);
        } else {
            result = sqlSession.selectMap(sqlCommand.getName(), param, methodSignature.getMapKey());
        }

        return result;
    }

    /**
     * 执行并获取游标
     *
     * @param sqlSession SQL 会话
     * @param args       参数
     * @param <T>        泛型
     * @return 游标
     */
    private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
        Cursor<T> result;

        Object param = methodSignature.convertArgsToSqlCommandParam(args);
        if (methodSignature.hasRowBounds()) {
            RowBounds rowBounds = methodSignature.extractRowBounds(args);
            result = sqlSession.selectCursor(sqlCommand.getName(), param, rowBounds);
        } else {
            result = sqlSession.selectCursor(sqlCommand.getName(), param);
        }

        return result;
    }

    /**
     * 参数 Map
     *
     * @param <V> 值泛型
     */
    public static class ParamMap<V> extends HashMap<String, V> {
        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + keySet());
            }

            return super.get(key);
        }
    }

    /**
     * Sql 指令
     */
    public static class SqlCommand {
        /**
         * 名称
         */
        private final String name;

        /**
         * 类型
         */
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            final String methodName = method.getName();
            final Class<?> declaringClass = method.getDeclaringClass();

            // 解析获取映射的语句
            MappedStatement mappedStatement =
                    resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);
            if (mappedStatement == null) {
                if (method.getAnnotation(Flush.class) != null) {
                    name = null;
                    type = SqlCommandType.FLUSH;
                } else {
                    throw new BindingException("Invalid bound statement (not found): "
                            + mapperInterface.getName() + "." + methodName);
                }
            } else {
                name = mappedStatement.getId();
                type = mappedStatement.getSqlCommandType();
                if (type == SqlCommandType.UNKOWN) {
                    throw new BindingException("Unknown execution method for: " + name);
                }
            }
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }

        /**
         * 解析映射的语句
         *
         * @param mapperInterface Mapper 接口
         * @param methodName      方法名
         * @param declaringClass  声明方法的类
         * @param configuration   配置
         * @return 映射的语句
         */
        private MappedStatement resolveMappedStatement(Class<?> mapperInterface,
                                                       String methodName,
                                                       Class<?> declaringClass,
                                                       Configuration configuration) {
            String statementId = mapperInterface.getName() + "." + methodName;
            if (configuration.hasMappedStatement(statementId)) {
                return configuration.getMappedStatement(statementId);
            }

            if (mapperInterface.equals(declaringClass)) {
                return null;
            }

            for (Class<?> superInterface : mapperInterface.getInterfaces()) {
                if (declaringClass.isAssignableFrom(superInterface)) {
                    MappedStatement mappedStatement = resolveMappedStatement(
                            superInterface, methodName, declaringClass, configuration);
                    if (mappedStatement != null) {
                        return mappedStatement;
                    }
                }
            }

            return null;
        }
    }

    /**
     * 方法签名
     */
    public static class MethodSignature {
        /**
         * 是否返回多个
         */
        private final boolean returnsMany;

        /**
         * 是否返回 map
         */
        private final boolean returnsMap;

        /**
         * 是否返回空
         */
        private final boolean returnsVoid;

        /**
         * 是否返回游标
         */
        private final boolean returnsCursor;

        /**
         * 是否返回 Optional
         */
        private final boolean returnsOptional;

        /**
         * 返回 Class 实例
         */
        private final Class<?> returnType;

        /**
         * map key
         */
        private final String mapKey;

        /**
         * 结果处理器索引
         */
        private final Integer resultHandlerIndex;

        /**
         * 行绑定索引
         */
        private final Integer rowBoundsIndex;

        /**
         * 参数名解析器
         */
        private final ParamNameResolver paramNameResolver;

        public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
            Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
            if (resolvedReturnType instanceof Class<?>) {
                this.returnType = (Class<?>) resolvedReturnType;
            } else if (resolvedReturnType instanceof ParameterizedType) {
                this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
            } else {
                this.returnType = method.getReturnType();
            }

            this.returnsVoid = void.class.equals(this.returnType);
            this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType)
                    || this.returnType.isArray();
            this.returnsCursor = Cursor.class.equals(this.returnType);
            this.returnsOptional = Optional.class.equals(this.returnType);
            this.mapKey = getMapKey(method);
            this.returnsMap = this.mapKey != null;
            this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
            this.resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
            this.paramNameResolver = new ParamNameResolver(configuration, method);
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public String getMapKey() {
            return mapKey;
        }

        public boolean returnsMany() {
            return returnsMany;
        }

        public boolean returnsMap() {
            return returnsMap;
        }

        public boolean returnsVoid() {
            return returnsVoid;
        }

        public boolean returnsCursor() {
            return returnsCursor;
        }

        public boolean returnsOptional() {
            return returnsOptional;
        }

        public boolean hasRowBounds() {
            return rowBoundsIndex != null;
        }

        public RowBounds extractRowBounds(Object[] args) {
            return hasRowBounds() ?
                    (RowBounds) args[rowBoundsIndex] : null;
        }

        public boolean hasResultHandler() {
            return resultHandlerIndex != null;
        }

        public ResultHandler extractResultHandler(Object[] args) {
            return hasResultHandler() ?
                    (ResultHandler) args[resultHandlerIndex] : null;
        }

        public Object convertArgsToSqlCommandParam(Object[] args) {
            return paramNameResolver.getNamedParams(args);
        }

        /**
         * 获取参数索引
         *
         * @param method    方法
         * @param paramType 参数类型
         * @return 参数索引
         */
        public Integer getUniqueParamIndex(Method method, Class<?> paramType) {
            Integer index = null;

            final Class<?>[] argTypes = method.getParameterTypes();
            for (int i = 0; i < argTypes.length; i++) {
                if (paramType.isAssignableFrom(argTypes[i])) {
                    if (index == null) {
                        index = i;
                    } else {
                        throw new BindingException(method.getName() + " cannot have multiple "
                                + paramType.getSimpleName() + " parameters");
                    }
                }
            }

            return index;
        }

        /**
         * 获取 key
         *
         * @param method 方法
         * @return key
         */
        private String getMapKey(Method method) {
            if (!Map.class.isAssignableFrom(method.getReturnType())) {
                return null;
            }

            final MapKey mapKeyAnnotation = method.getAnnotation(MapKey.class);
            if (mapKeyAnnotation != null) {
                return mapKeyAnnotation.value();
            }

            return null;
        }
    }
}
