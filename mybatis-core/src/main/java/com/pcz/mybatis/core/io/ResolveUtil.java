package com.pcz.mybatis.core.io;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 解析工具类
 *
 * @author picongzhi
 */
public class ResolveUtil<T> {
    /**
     * 匹配的 Class 实例
     */
    private Set<Class<? extends T>> matches = new HashSet<>();

    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    /**
     * 获取匹配的 Class 实例
     *
     * @return 匹配的 Class 实例
     */
    public Set<Class<? extends T>> getClasses() {
        return matches;
    }

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    public ClassLoader getClassLoader() {
        return classLoader == null ?
                Thread.currentThread().getContextClassLoader() :
                classLoader;
    }

    /**
     * 设置类加载器
     *
     * @param classLoader 类加载器
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 扫描多个包，获取所有实现了指定类的类
     *
     * @param parent       父类
     * @param packageNames 包名
     * @return 解析工具类实例
     */
    public ResolveUtil<T> findImplementations(Class<?> parent, String... packageNames) {
        if (packageNames == null) {
            return this;
        }

        Test test = new IsA(parent);
        for (String packageName : packageNames) {
            find(test, packageName);
        }

        return this;
    }

    /**
     * 扫描多个包，获取所有注解了指定注解的类
     *
     * @param annotation   注解
     * @param packageNames 包名
     * @return 解析工具类实例
     */
    public ResolveUtil<T> findAnnotated(Class<? extends Annotation> annotation, String... packageNames) {
        if (packageNames == null) {
            return this;
        }

        Test test = new AnnotatedWith(annotation);
        for (String packageName : packageNames) {
            find(test, packageName);
        }

        return this;
    }

    /**
     * 扫描包，获取所有符合测试条件的 Class 实例
     * 如果 test 返回 true，可通过 {@link #getClasses()} 获取匹配的 Class 实例
     *
     * @param test        测试条件
     * @param packageName 包名
     * @return 解析工具类实例
     */
    public ResolveUtil<T> find(Test test, String packageName) {
        String path = getPackagePath(packageName);

        try {
            List<String> children = VFS.getInstance().list(path);
            for (String child : children) {
                if (child.endsWith(".class")) {
                    addIfMatching(test, child);
                }
            }
        } catch (IOException e) {
            // TODO: 打印日志
        }

        return this;
    }

    /**
     * 获取包路径
     *
     * @param packageName 包名
     * @return 包路径
     */
    protected String getPackagePath(String packageName) {
        return packageName == null ?
                null : packageName.replace('.', '/');
    }

    /**
     * 满足条件添加
     *
     * @param test      测试条件
     * @param classPath 类路径
     */
    @SuppressWarnings("unchecked")
    protected void addIfMatching(Test test, String classPath) {
        try {
            // 去掉 .java，再将 / 转成 .
            String className = classPath.substring(0, classPath.indexOf('.'))
                    .replace('/', '.');
            ClassLoader classLoader = getClassLoader();

            Class<?> cls = classLoader.loadClass(className);
            if (test.matches(cls)) {
                matches.add((Class<T>) cls);
            }
        } catch (Throwable t) {
            // TODO：打印日志
        }
    }

    /**
     * 检测接口
     */
    public interface Test {
        /**
         * 判断是否匹配
         *
         * @param type Class 实例
         * @return 是否匹配
         */
        boolean matches(Class<?> type);
    }

    /**
     * 检测是否继承自指定的类
     */
    public static class IsA implements Test {
        /**
         * 父类
         */
        private Class<?> parent;

        public IsA(Class<?> parent) {
            this.parent = parent;
        }

        @Override
        public boolean matches(Class<?> type) {
            return type != null && parent.isAssignableFrom(type);
        }

        @Override
        public String toString() {
            return "is assignable to " + parent.getSimpleName();
        }
    }

    /**
     * 检测是否注解了指定注解
     */
    public static class AnnotatedWith implements Test {
        /**
         * 注解
         */
        private Class<? extends Annotation> annotation;

        public AnnotatedWith(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }

        @Override
        public boolean matches(Class<?> type) {
            return type != null && type.isAnnotationPresent(annotation);
        }

        @Override
        public String toString() {
            return "annotated with @" + annotation.getSimpleName();
        }
    }
}
