package com.pcz.mybatis.core.reflection;

import com.pcz.mybatis.core.reflection.invoker.*;
import com.pcz.mybatis.core.reflection.property.PropertyNamer;
import com.pcz.mybatis.core.util.MapUtil;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * 反射器
 *
 * @author picongzhi
 */
public class Reflector {
    /**
     * 类型
     */
    private final Class<?> type;

    /**
     * get 方法
     */
    private final Map<String, Invoker> getMethods = new HashMap<>();

    /**
     * set 方法
     */
    private final Map<String, Invoker> setMethods = new HashMap<>();

    /**
     * get 类型
     */
    private final Map<String, Class<?>> getTypes = new HashMap<>();

    /**
     * set 类型
     */
    private final Map<String, Class<?>> setTypes = new HashMap<>();

    /**
     * 可读属性名
     */
    private final String[] readablePropertyNames;

    /**
     * 可写属性名
     */
    private final String[] writablePropertyNames;

    /**
     * 默认的构造器
     */
    private Constructor<?> defaultConstructor;

    /**
     * 大小写不敏感的属性
     */
    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    public Reflector(Class<?> cls) {
        this.type = cls;

        // 添加默认的构造器
        addDefaultConstructor(cls);

        // 获取类的所有方法
        Method[] classMethods = getClassMethods(cls);

        // 添加 getter setter
        addGetMethods(classMethods);
        addSetMethods(classMethods);
        addFields(cls);

        readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        for (String name : readablePropertyNames) {
            caseInsensitivePropertyMap.put(name.toUpperCase(Locale.ENGLISH), name);
        }

        writablePropertyNames = setMethods.keySet().toArray(new String[0]);
        for (String name : writablePropertyNames) {
            caseInsensitivePropertyMap.put(name.toUpperCase(Locale.ENGLISH), name);
        }
    }

    /**
     * 获取反射器类型
     *
     * @return 反射器类型
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * 获取属性名
     *
     * @param name 属性名
     * @return 属性名
     */
    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }

    /**
     * 判断是否有 getter
     *
     * @param propertyName 属性名
     * @return 是否有 getter
     */
    public boolean hasGetter(String propertyName) {
        return getMethods.containsKey(propertyName);
    }

    /**
     * 判断是否有 setter
     *
     * @param propertyName 属性名
     * @return 是否有 setter
     */
    public boolean hasSetter(String propertyName) {
        return setMethods.containsKey(propertyName);
    }

    /**
     * 获取 getter 类型
     *
     * @param propertyName 属性名
     * @return getter 类型
     */
    public Class<?> getGetterType(String propertyName) {
        Class<?> cls = getTypes.get(propertyName);
        if (cls == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }

        return cls;
    }

    /**
     * 获取 setter 类型
     *
     * @param propertyName 属性名
     * @return setter 类型
     */
    public Class<?> getSetterType(String propertyName) {
        Class<?> cls = setTypes.get(propertyName);
        if (cls == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }

        return cls;
    }

    /**
     * 获取可读属性名
     *
     * @return 可读属性名
     */
    public String[] getReadablePropertyNames() {
        return readablePropertyNames;
    }

    /**
     * 获取可写属性名
     *
     * @return 可写属性名
     */
    public String[] getWritablePropertyNames() {
        return writablePropertyNames;
    }

    /**
     * 获取 get invoker
     *
     * @param propertyName 属性名
     * @return get invoker
     */
    public Invoker getGetInvoker(String propertyName) {
        Invoker invoker = getMethods.get(propertyName);
        if (invoker == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }

        return invoker;
    }

    /**
     * 获取 set invoker
     *
     * @param propertyName 属性名
     * @return set invoker
     */
    public Invoker getSetInvoker(String propertyName) {
        Invoker invoker = setMethods.get(propertyName);
        if (invoker == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }

        return invoker;
    }

    /**
     * 判断是否有默认构造器
     *
     * @return 是否有默认构造器
     */
    public boolean hasDefaultConstructor() {
        return defaultConstructor != null;
    }

    /**
     * 添加默认的构造器
     *
     * @param cls Class 实例
     */
    private void addDefaultConstructor(Class<?> cls) {
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterTypes().length == 0)
                .findAny()
                .ifPresent(constructor -> this.defaultConstructor = constructor);
    }

    /**
     * 获取类的所有方法，包括父类和接口的方法
     *
     * @param cls Class 实例
     * @return 所有方法
     */
    private Method[] getClassMethods(Class<?> cls) {
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = cls;
        while (currentClass != null && currentClass != Object.class) {
            // 添加当前类的方法
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            // 获取所有接口
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> interfaceCls : interfaces) {
                // 添加接口的方法
                addUniqueMethods(uniqueMethods, interfaceCls.getMethods());
            }

            // 遍历父类
            currentClass = currentClass.getSuperclass();
        }

        return uniqueMethods.values().toArray(new Method[0]);
    }

    /**
     * 添加唯一的方法
     *
     * @param uniqueMethods 唯一方法映射
     * @param methods       方法
     */
    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method method : methods) {
            if (!method.isBridge()) {
                // 获取方法签名
                String signature = getSignature(method);

                // 添加方法
                if (!uniqueMethods.containsKey(signature)) {
                    uniqueMethods.put(signature, method);
                }
            }
        }
    }

    /**
     * 获取方法签名
     * 格式为：返回类型名#方法名:参数1,参数2
     *
     * @param method 方法实例
     * @return 方法签名
     */
    private String getSignature(Method method) {
        // 获取返回类型
        Class<?> returnType = method.getReturnType();
        StringBuilder builder = new StringBuilder()
                .append(returnType.getName())
                .append('#')
                .append(method.getName());

        // 方法参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            builder.append(i == 0 ? ':' : ',')
                    .append(parameterTypes[i].getName());
        }

        return builder.toString();
    }

    /**
     * 添加 get 方法
     *
     * @param methods 方法
     */
    private void addGetMethods(Method[] methods) {
        // 聚合冲突的 get 方法
        Map<String, List<Method>> conflictingGetters = new HashMap<>();
        Arrays.stream(methods)
                .filter(method -> method.getParameterTypes().length == 0
                        && PropertyNamer.isGetter(method.getName()))
                .forEach(method -> addMethodConflict(
                        conflictingGetters, PropertyNamer.methodToProperty(method.getName()), method));

        // 解决 get 方法冲突
        resolveGetterConflicts(conflictingGetters);
    }

    /**
     * 解决 get 方法冲突
     *
     * @param conflictingGetters 冲突的 get 方法
     */
    private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (Map.Entry<String, List<Method>> entry : conflictingGetters.entrySet()) {
            Method winner = null;
            boolean isAmbiguous = false;

            for (Method candidate : entry.getValue()) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }

                Class<?> winnerType = winner.getReturnType();
                Class<?> candidateType = candidate.getReturnType();
                if (candidateType.equals(winnerType)) {
                    if (!boolean.class.equals(candidateType)) {
                        isAmbiguous = true;
                        break;
                    } else if (candidate.getName().startsWith("is")) {
                        winner = candidate;
                    }
                } else if (candidateType.isAssignableFrom(winnerType)) {

                } else if (winnerType.isAssignableFrom(candidateType)) {
                    winner = candidate;
                } else {
                    isAmbiguous = true;
                    break;
                }
            }

            String name = entry.getKey();
            addGetMethod(name, winner, isAmbiguous);
        }
    }

    /**
     * 添加 set 方法
     *
     * @param methods 方法
     */
    private void addSetMethods(Method[] methods) {
        // 聚合冲突的 set 方法
        Map<String, List<Method>> conflictingSetters = new HashMap<>();
        Arrays.stream(methods)
                .filter(method -> method.getParameterTypes().length == 1
                        && PropertyNamer.isSetter(method.getName()))
                .forEach(method -> addMethodConflict(
                        conflictingSetters, PropertyNamer.methodToProperty(method.getName()), method));

        // 解决 set 冲突
        resolveSetterConflict(conflictingSetters);
    }

    /**
     * 解决 setter 冲突
     *
     * @param conflictingSetters 冲突的 setter
     */
    private void resolveSetterConflict(Map<String, List<Method>> conflictingSetters) {
        for (Map.Entry<String, List<Method>> entry : conflictingSetters.entrySet()) {
            String name = entry.getKey();
            List<Method> setters = entry.getValue();

            Class<?> getterType = getTypes.get(name);

            boolean isGetterAmbiguous = getMethods.get(name) instanceof AmbiguousMethodInvoker;
            boolean isSetterAmbiguous = false;

            Method match = null;
            for (Method setter : setters) {
                if (!isGetterAmbiguous && setter.getParameterTypes()[0].equals(getterType)) {
                    match = setter;
                    break;
                }

                if (!isSetterAmbiguous) {
                    match = pickBetterSetter(match, setter, name);
                    isSetterAmbiguous = match == null;
                }
            }

            if (match != null) {
                addSetMethod(name, match);
            }
        }
    }

    /**
     * 选择更好的 setter
     *
     * @param current   当前
     * @param candidate 候选
     * @param name      属性名
     * @return 更好的 setter
     */
    private Method pickBetterSetter(Method current, Method candidate, String name) {
        if (current == null) {
            return candidate;
        }

        Class<?> currentType = current.getParameterTypes()[0];
        Class<?> candidateType = candidate.getParameterTypes()[0];
        if (currentType.isAssignableFrom(candidateType)) {
            return candidate;
        } else if (candidateType.isAssignableFrom(currentType)) {
            return current;
        }

        MethodInvoker methodInvoker = new AmbiguousMethodInvoker(current,
                MessageFormat.format("Ambiguous setters defined for property ''{0}'' in class ''{1}'' with types ''{2}'' and ''{3}''",
                        name, candidate.getDeclaringClass().getName(), currentType.getName(), candidateType.getName()));
        setMethods.put(name, methodInvoker);

        Type[] paramTypes = TypeParameterResolver.resolveParamTypes(current);
        setTypes.put(name, typeToClass(paramTypes[0]));

        return null;
    }

    /**
     * 添加 set 方法
     *
     * @param name   名称
     * @param method set 方法
     */
    private void addSetMethod(String name, Method method) {
        MethodInvoker methodInvoker = new MethodInvoker(method);
        setMethods.put(name, methodInvoker);

        Type[] paramTypes = TypeParameterResolver.resolveParamTypes(method);
        setTypes.put(name, typeToClass(paramTypes[0]));
    }

    /**
     * 添加字段
     *
     * @param cls Class 实例
     */
    private void addFields(Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (!setMethods.containsKey(field.getName())) {
                int modifiers = field.getModifiers();
                if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
                    addSetField(field);
                }
            }

            if (!getMethods.containsKey(field.getName())) {
                addGetField(field);
            }
        }

        if (cls.getSuperclass() != null) {
            addFields(cls.getSuperclass());
        }
    }

    /**
     * 添加 set 字段
     *
     * @param field 字段
     */
    private void addSetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            setMethods.put(field.getName(), new SetFieldInvoker(field));

            Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
            setTypes.put(field.getName(), typeToClass(fieldType));
        }
    }

    /**
     * 添加 get 字段
     *
     * @param field 字段
     */
    private void addGetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            getMethods.put(field.getName(), new GetFieldInvoker(field));

            Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
            getTypes.put(field.getName(), typeToClass(fieldType));
        }
    }

    /**
     * 添加冲突的方法
     *
     * @param conflictingMethods 冲突的方法
     * @param name               属性名
     * @param method             方法
     */
    private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        if (isValidPropertyName(name)) {
            List<Method> methods = MapUtil.compoteIfAbsent(conflictingMethods, name, key -> new ArrayList<>());
            methods.add(method);
        }
    }

    /**
     * 判断是否是合法的属性名
     *
     * @param name 属性名
     * @return 是否是合法的属性名
     */
    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    /**
     * 添加 get 方法
     *
     * @param name        属性名
     * @param method      get 方法
     * @param isAmbiguous 是否模糊
     */
    private void addGetMethod(String name, Method method, boolean isAmbiguous) {
        MethodInvoker methodInvoker = isAmbiguous
                ? new AmbiguousMethodInvoker(method, MessageFormat.format(
                "Illegal overload getter method with ambiguous type for property ''{0}'' in class ''{1}''."
                        + " This breaks the JavaBeans specification and can cause unpredictable results.",
                name, method.getDeclaringClass().getName()))
                : new MethodInvoker(method);
        getMethods.put(name, methodInvoker);

        Type returnType = TypeParameterResolver.resolveReturnType(method, type);
        getTypes.put(name, typeToClass(returnType));
    }

    /**
     * Type 转 Class
     *
     * @param type Type
     * @return Class
     */
    private Class<?> typeToClass(Type type) {
        Class<?> result = null;

        if (type instanceof Class) {
            result = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            result = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (componentType instanceof Class) {
                result = Array.newInstance((Class<?>) componentType, 0).getClass();
            } else {
                Class<?> componentClass = typeToClass(componentType);
                result = Array.newInstance(componentClass, 0).getClass();
            }
        }

        if (result == null) {
            result = Object.class;
        }

        return result;
    }

    /**
     * 判断是否可以控制成员权限
     *
     * @return 是否可以控制成员权限
     */
    public static boolean canControlMemberAccessible() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }

        return true;
    }
}
