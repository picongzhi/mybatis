package com.pcz.mybatis.core.io;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 资源
 *
 * @author picongzhi
 */
public class Resources {
    /**
     * 类加载器包装
     */
    private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    /**
     * 字符集
     */
    private static Charset charset;

    Resources() {
    }

    /**
     * 获取默认的类加载器
     *
     * @return 默认的类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        return classLoaderWrapper.defaultClassLoader;
    }

    /**
     * 设置默认的类加载器
     *
     * @param defaultClassLoader 默认的类加载器
     */
    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        classLoaderWrapper.defaultClassLoader = defaultClassLoader;
    }

    /**
     * 获取资源 URL
     *
     * @param resource 资源路径
     * @return URL
     * @throws IOException IO 异常
     */
    public static URL getResourceURL(String resource) throws IOException {
        return getResourceURL(null, resource);
    }

    /**
     * 获取资源 URL
     *
     * @param classLoader 类加载器
     * @param resource    资源路径
     * @return URL
     * @throws IOException IO 异常
     */
    public static URL getResourceURL(ClassLoader classLoader, String resource) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource, classLoader);
        if (url == null) {
            throw new IOException("Could noy find resource " + resource);
        }

        return url;
    }

    /**
     * 根据资源获取输入流
     *
     * @param resource 资源路径
     * @return 输入流
     * @throws IOException IO异常
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    /**
     * 根据资源获取输入流
     *
     * @param classLoader 类加载器
     * @param resource    资源路径
     * @return 输入流
     * @throws IOException IO异常
     */
    public static InputStream getResourceAsStream(ClassLoader classLoader, String resource) throws IOException {
        InputStream inputStream = classLoaderWrapper.getResourceAsStream(resource, classLoader);
        if (inputStream == null) {
            throw new IOException("Could not find resource " + resource);
        }

        return inputStream;
    }

    /**
     * 获取资源获取属性
     *
     * @param resource 资源路径
     * @return 属性
     * @throws IOException IO异常
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getResourceAsStream(resource)) {
            properties.load(inputStream);
        }

        return properties;
    }

    /**
     * 根据资源获取属性
     *
     * @param classLoader 类加载器
     * @param resource    资源路径
     * @return 属性
     * @throws IOException IO 异常
     */
    public static Properties getResourceAsProperties(ClassLoader classLoader, String resource) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getResourceAsStream(classLoader, resource)) {
            properties.load(inputStream);
        }

        return properties;
    }

    /**
     * 根据资源获取 Reader
     *
     * @param resource 资源
     * @return Reader
     * @throws IOException IO 异常
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        if (charset == null) {
            return new InputStreamReader(getResourceAsStream(resource));
        } else {
            return new InputStreamReader(getResourceAsStream(resource), charset);
        }
    }

    /**
     * 根据资源获取 Reader
     *
     * @param classLoader ClassLoader
     * @param resource    资源
     * @return Reader
     * @throws IOException IO 异常
     */
    public static Reader getResourceAsReader(ClassLoader classLoader, String resource) throws IOException {
        if (charset == null) {
            return new InputStreamReader(getResourceAsStream(classLoader, resource));
        } else {
            return new InputStreamReader(getResourceAsStream(classLoader, resource), charset);
        }
    }

    /**
     * 根据资源获取文件
     *
     * @param resource 资源
     * @return 文件
     * @throws IOException IO 异常
     */
    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceURL(resource).getFile());
    }

    /**
     * 根据资源获取文件
     *
     * @param classLoader ClassLoader
     * @param resource    资源
     * @return 文件
     * @throws IOException IO 异常
     */
    public static File getResourceAsFile(ClassLoader classLoader, String resource) throws IOException {
        return new File(getResourceURL(classLoader, resource).getFile());
    }

    /**
     * 根据 url 获取输入流
     *
     * @param urlString url
     * @return 输入流
     * @throws IOException UI异常
     */
    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();

        return urlConnection.getInputStream();
    }

    /**
     * 根据 url 获取 Reader
     *
     * @param url url
     * @return Reader
     * @throws IOException IO 异常
     */
    public static Reader getUrlAsReader(String url) throws IOException {
        if (charset == null) {
            return new InputStreamReader(getUrlAsStream(url));
        } else {
            return new InputStreamReader(getUrlAsStream(url), charset);
        }
    }

    /**
     * 根据 url 获取属性
     *
     * @param url url
     * @return 属性
     * @throws IOException IO异常
     */
    public static Properties getUrlAsProperties(String url) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getUrlAsStream(url)) {
            properties.load(inputStream);
        }

        return properties;
    }

    /**
     * 根据类名获取类实例
     *
     * @param className 类名
     * @return 类实例
     * @throws ClassNotFoundException 类没找到异常
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }

    /**
     * 获取字符集
     *
     * @return 字符集
     */
    public static Charset getCharset() {
        return charset;
    }

    /**
     * 设置字符集
     *
     * @param charset 字符集
     */
    public static void setCharset(Charset charset) {
        Resources.charset = charset;
    }
}
