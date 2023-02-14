package com.pcz.mybatis.core.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 虚拟文件系统
 *
 * @author picongzhi
 */
public abstract class VFS {
    /**
     * 内置的实现类
     */
    public static final Class<?>[] IMPLEMENTATIONS = {DefaultVFS.class};

    /**
     * 用户实现类
     * 通过 {@link #addImplClass(Class)} 添加
     */
    public static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<>();

    /**
     * 获取 VFS 实例
     *
     * @return VFS 实例
     */
    public static VFS getInstance() {
        return VFSHolder.INSTANCE;
    }

    /**
     * 添加实现类
     *
     * @param cls VFS 实现类
     */
    public static void addImplClass(Class<? extends VFS> cls) {
        if (cls != null) {
            USER_IMPLEMENTATIONS.add(cls);
        }
    }

    /**
     * 获取 Class 实例，如果不存在返回 null
     *
     * @param className Class 名称
     * @return Class 实例
     */
    protected static Class<?> getClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取方法，不存在返回 null
     *
     * @param cls            Class 实例
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 方法实例
     */
    protected static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        if (cls == null) {
            return null;
        }

        try {
            return cls.getMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 执行方法
     *
     * @param method     方法实例
     * @param object     目标对象
     * @param parameters 参数
     * @param <T>        返回泛型
     * @return 执行结果
     * @throws IOException      IO 异常
     * @throws RuntimeException 运行时异常
     */
    protected static <T> T invoke(Method method, Object object, Object... parameters)
            throws IOException, RuntimeException {
        try {
            return (T) method.invoke(object, parameters);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据路径获取资源 URL
     *
     * @param path 路径
     * @return 资源 URL
     * @throws IOException IO 异常
     */
    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(
                Thread.currentThread().getContextClassLoader().getResources(path));
    }

    /**
     * 判断是否合法
     *
     * @return 是否合法
     */
    public abstract boolean isValid();

    /**
     * 遍历获取路径下的所有子资源
     *
     * @param url     URL
     * @param forPath 路径
     * @return 所有子资源
     * @throws IOException IO 异常
     */
    protected abstract List<String> list(URL url, String forPath) throws IOException;

    /**
     * 遍历获取路径下的所有子资源
     *
     * @param path 路径
     * @return 所有子资源
     * @throws IOException IO 异常
     */
    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<>();
        for (URL url : getResources(path)) {
            names.addAll(list(url, path));
        }

        return names;
    }

    /**
     * VFS Holder
     */
    private static class VFSHolder {
        /**
         * VFS 实例
         */
        static final VFS INSTANCE = createVFS();

        /**
         * 创建 VFS
         *
         * @return VFS
         */
        @SuppressWarnings("unchecked")
        static VFS createVFS() {
            List<Class<? extends VFS>> impls = new ArrayList<>();
            impls.addAll(USER_IMPLEMENTATIONS);
            impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));

            VFS vfs = null;
            for (int i = 0; (vfs == null || !vfs.isValid()) && i < impls.size(); i++) {
                Class<? extends VFS> impl = impls.get(i);
                try {
                    vfs = impl.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException
                        | NoSuchMethodException | InvocationTargetException e) {
                    // TODO: 打印日志
                    return null;
                }
            }

            return vfs;
        }
    }
}
