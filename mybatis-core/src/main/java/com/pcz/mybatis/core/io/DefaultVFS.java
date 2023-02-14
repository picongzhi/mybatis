package com.pcz.mybatis.core.io;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 默认的 VFS
 *
 * @author picongzhi
 */
public class DefaultVFS extends VFS {
    /**
     * jar 的 magic header
     */
    private static final byte[] JAR_MAGIC = {'P', 'K', 3, 4};

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<String> list(URL url, String forPath) throws IOException {
        InputStream inputStream = null;

        try {
            URL jarURL = findJarForResource(url);
            if (jarURL != null) {
                // jar
                inputStream = jarURL.openStream();
                return listResources(new JarInputStream(inputStream), forPath);
            }

            // 非 jar
            List<String> children = new ArrayList<>();
            try {
                if (isJar(url)) {
                    inputStream = url.openStream();
                    try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
                        for (JarEntry jarEntry; (jarEntry = jarInputStream.getNextJarEntry()) != null; ) {
                            children.add(jarEntry.getName());
                        }
                    }
                } else {
                    inputStream = url.openStream();
                    List<String> lines = new ArrayList<>();
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        for (String line; (line = bufferedReader.readLine()) != null; ) {
                            lines.add(line);
                            if (getResources(forPath + "/" + line).isEmpty()) {
                                lines.clear();
                                break;
                            }
                        }
                    } catch (InvalidPathException | FileSystemException e) {
                        lines.clear();
                    }

                    if (!lines.isEmpty()) {
                        children.addAll(lines);
                    }
                }
            } catch (FileNotFoundException e) {
                if ("file".equals(url.getProtocol())) {
                    // file://xxx
                    File file = new File(url.getFile());
                    if (file.isDirectory()) {
                        children = Arrays.asList(file.list());
                    }
                } else {
                    throw e;
                }
            }

            String prefix = url.toExternalForm();
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }

            List<String> resources = new ArrayList<>();
            for (String child : children) {
                String resourcePath = forPath + "/" + child;
                resources.add(resourcePath);

                URL childUrl = new URL(prefix + child);
                resources.addAll(list(childUrl, resourcePath));
            }

            return resources;
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // 忽略
                }
            }
        }
    }

    /**
     * 获取 jar
     *
     * @param url URL
     * @return jar
     */
    protected URL findJarForResource(URL url) {
        // 获取 file 部分
        boolean continueLoop = true;
        while (continueLoop) {
            try {
                url = new URL(url.getFile());
            } catch (MalformedURLException e) {
                continueLoop = false;
            }
        }

        // 去掉 .jar 后面的部分
        StringBuilder jarUrl = new StringBuilder(url.toExternalForm());
        int index = jarUrl.lastIndexOf(".jar");
        if (index >= 0) {
            jarUrl.setLength(index + 4);
        } else {
            return null;
        }

        // 打开 jar 并测试
        try {
            URL testUrl = new URL(jarUrl.toString());
            if (isJar(testUrl)) {
                // 是 jar，直接返回
                return testUrl;
            } else {
                jarUrl.replace(0, jarUrl.length(), testUrl.getFile());
                File file = new File(jarUrl.toString());

                if (!file.exists()) {
                    // 文件不存在，文件名可能被 URL 编码，尝试获取编码后的文件名
                    try {
                        file = new File(URLEncoder.encode(jarUrl.toString(), StandardCharsets.UTF_8.name()));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("Unsupported encoding? UTF-8? That's impossible.");
                    }
                }

                if (file.exists()) {
                    // 文件存在
                    testUrl = file.toURI().toURL();
                    if (isJar(testUrl)) {
                        // 是 jar
                        return testUrl;
                    }
                }
            }
        } catch (MalformedURLException e) {
            // TODO: log.error
        }

        return null;
    }

    /**
     * 判断是否是 jar
     *
     * @param url URL
     * @return 是否是 jar
     */
    protected boolean isJar(URL url) {
        return isJar(url, new byte[JAR_MAGIC.length]);
    }

    /**
     * 判断是否是 jar
     *
     * @param url    URL
     * @param buffer 缓存
     * @return 是否是 jar
     */
    protected boolean isJar(URL url, byte[] buffer) {
        try (InputStream inputStream = url.openStream()) {
            // 读取前4字节
            inputStream.read(buffer, 0, JAR_MAGIC.length);
            if (Arrays.equals(buffer, JAR_MAGIC)) {
                return true;
            }
        } catch (Exception e) {
            // 忽略
        }

        return false;
    }

    /**
     * 获取所有资源
     *
     * @param jarInputStream jar 输入流
     * @param path           路径
     * @return 资源
     * @throws IOException IO 异常
     */
    protected List<String> listResources(JarInputStream jarInputStream, String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        List<String> resources = new ArrayList<>();
        for (JarEntry jarEntry; (jarEntry = jarInputStream.getNextJarEntry()) != null; ) {
            if (!jarEntry.isDirectory()) {
                StringBuilder name = new StringBuilder(jarEntry.getName());
                if (name.charAt(0) != '/') {
                    name.insert(0, '/');
                }

                if (name.indexOf(path) == 0) {
                    resources.add(name.substring(1));
                }
            }
        }

        return resources;
    }
}
