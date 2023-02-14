package com.pcz.mybatis.core.io;

import java.io.InputStream;
import java.net.URL;

/**
 * 类加载器包装
 *
 * @author picongzhi
 */
public class ClassLoaderWrapper {
    /**
     * 默认的类加载器
     */
    ClassLoader defaultClassLoader;

    /**
     * 系统类加载器
     */
    ClassLoader systemClassLoader;

    ClassLoaderWrapper() {
        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException e) {
            // do nothing
        }
    }

    /**
     * 获取 URL
     *
     * @param resource 资源路径
     * @return URL
     */
    public URL getResourceAsURL(String resource) {
        return getResourceAsURL(resource, getClassLoaders(null));
    }

    /**
     * 获取 URL
     *
     * @param resource    资源路径
     * @param classLoader 类加载器
     * @return URL
     */
    public URL getResourceAsURL(String resource, ClassLoader classLoader) {
        return getResourceAsURL(resource, getClassLoaders(classLoader));
    }

    /**
     * 获取 URL
     *
     * @param resource     资源路径
     * @param classLoaders 类加载器
     * @return URL
     */
    URL getResourceAsURL(String resource, ClassLoader[] classLoaders) {
        URL url;
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                url = classLoader.getResource(resource);
                if (url == null) {
                    // 部分 ClassLoader 需要前置"/"
                    url = classLoader.getResource("/" + resource);
                }

                if (url != null) {
                    return url;
                }
            }
        }

        return null;
    }

    /**
     * 获取输入流
     *
     * @param resource 资源路径
     * @return 输入流
     */
    public InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    /**
     * 获取输入流
     *
     * @param resource    资源路径
     * @param classLoader 类加载器
     * @return 输入流
     */
    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    /**
     * 获取输入流
     *
     * @param resource     资源路径
     * @param classLoaders 类加载器
     * @return 输入流
     */
    InputStream getResourceAsStream(String resource, ClassLoader[] classLoaders) {
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                InputStream inputStream = classLoader.getResourceAsStream(resource);
                if (inputStream == null) {
                    // 部分 ClassLoader 需要前置"/"
                    inputStream = classLoader.getResourceAsStream("/" + resource);
                }

                if (inputStream != null) {
                    return inputStream;
                }
            }
        }

        return null;
    }

    /**
     * 根据类名获取类实例
     *
     * @param name 类名
     * @return 类实例
     * @throws ClassNotFoundException 类没找到异常
     */
    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    /**
     * 根据类名获取类实例
     *
     * @param name        类名
     * @param classLoader 类加载器
     * @return 类实例
     * @throws ClassNotFoundException 类没找到异常
     */
    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    /**
     * 根据类名获取类实例
     *
     * @param name         类名
     * @param classLoaders 类加载器
     * @return 类实例
     * @throws ClassNotFoundException 类没找到异常
     */
    Class<?> classForName(String name, ClassLoader[] classLoaders) throws ClassNotFoundException {
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                try {
                    return Class.forName(name, true, classLoader);
                } catch (ClassNotFoundException e) {

                }
            }
        }

        throw new ClassNotFoundException("Cannot find class: " + name);
    }

    /**
     * 获取类加载器
     *
     * @param classLoader 输入类加载
     * @return 类加载器
     */
    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[]{
                classLoader,
                defaultClassLoader,
                Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(),
                systemClassLoader
        };
    }
}
